package it.uninaSocialGroup.DAO;

import it.uninaSocialGroup.Oggetti.Group;
import javafx.scene.control.Label;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;
import java.sql.*;

public class GroupDAO {
    private Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }
    public Map<Integer, Group> getGroupsByUser(String userName) {
        Map<Integer, Group> groups = new HashMap<>();
        String query = "SELECT groupDetails.id_gruppo, groupDetails.fotoGruppo, groupDetails.nome " +
                "FROM gruppi AS groupDetails " +
                "INNER JOIN group_members AS participantDetails ON groupDetails.id_gruppo = participantDetails.id_gruppo " +
                "WHERE participantDetails.nomeUtente = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_gruppo");
                    String fotoGruppo = resultSet.getString("fotoGruppo");
                    String nome = resultSet.getString("nome");
                    groups.put(id, new Group(id, nome, fotoGruppo));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public Map<Integer, Group> searchGroups(String searchCriteria) {
        Map<Integer, Group> groups = new HashMap<>();
        String query = "SELECT id_gruppo, fotoGruppo, nome FROM gruppi WHERE LOWER(nome) LIKE ? OR LOWER(categoria) LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchCriteria.toLowerCase() + "%");
            statement.setString(2, "%" + searchCriteria.toLowerCase() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_gruppo");
                    String fotoGruppo = resultSet.getString("fotoGruppo");
                    String nome = resultSet.getString("nome");
                    groups.put(id, new Group(id, nome, fotoGruppo));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }
    public Group getGroupDetailsById(int groupId) {
        String query = "SELECT fotoGruppo, nome, descrizione, categoria FROM gruppi WHERE id_gruppo = ?";
        Group group = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String photoLink = resultSet.getString("fotoGruppo");
                    String name = resultSet.getString("nome");
                    String description = resultSet.getString("descrizione");
                    String category = resultSet.getString("categoria");
                    group = new Group(groupId, name, photoLink, description, category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }

    public void createGroup(Group group) {
        String sql = "INSERT INTO gruppi (nome, nomeCreatore, descrizione, categoria, dataCreazione, fotoGruppo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getGroupName());
            stmt.setString(2, group.getAdmin());
            stmt.setString(3, group.getDescription());
            stmt.setString(4, group.getCategory());
            stmt.setDate(5, new java.sql.Date(group.getCreationDate().getTime()));
            stmt.setString(6, group.getGroupPictureLink());
            stmt.executeUpdate();

            // Gestione dell'associazione membro gruppo
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int groupId = generatedKeys.getInt(1);
                    addGroupMember(groupId, group.getAdmin());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gestione dell'errore
        }
    }

    private void addGroupMember(int groupId, String username) throws SQLException {
        String sqlGroupMembers = "INSERT INTO group_members (id_gruppo, nomeUtente) VALUES (?, ?)";
        try (PreparedStatement stmtGroupMembers = connection.prepareStatement(sqlGroupMembers)) {
            stmtGroupMembers.setInt(1, groupId);
            stmtGroupMembers.setString(2, username);
            stmtGroupMembers.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            throw new SQLException("Errore nell'aggiunta del membro al gruppo");
        }
    }
    public boolean checkIfNameIsAlreadyUsed(String groupName) throws SQLException {
        String sql = "SELECT * FROM gruppi WHERE nome = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Errore nella verifica del nome del gruppo");
        }
        return false;
    }
}
