package it.uninaSocialGroup.DAO;

import it.uninaSocialGroup.Utils.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {
    private Connection connection;

    public GroupMemberDAO(Connection connection) {
        this.connection = connection;
    }

    public void joinGroup(int groupId, String userName) {
        String query = "INSERT INTO group_members (id_gruppo, nomeUtente) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setString(2, userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserMemberOfGroup(int groupId, String userName) {
        String query = "SELECT * FROM group_members WHERE id_gruppo = ? AND nomeUtente = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);
            statement.setString(2, userName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getGroupMembers(int groupId) {
        List<String> members = new ArrayList<>();
        String query = "SELECT nomeUtente FROM group_members WHERE id_gruppo = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, groupId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(resultSet.getString("nomeUtente"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }
}
