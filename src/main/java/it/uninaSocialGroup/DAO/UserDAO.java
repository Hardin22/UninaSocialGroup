package it.uninaSocialGroup.DAO;
import it.uninaSocialGroup.Oggetti.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }
    public User getUserByUsername(String username) {
        User user = null;
        String query = "SELECT nomeutente, nome, email, cognome, datadinascita, fotoprofilo, matricola FROM users WHERE nomeutente = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("nomeutente"), rs.getString("nome"), rs.getString("cognome"), rs.getString("email"), rs.getDate("datadinascita").toLocalDate(), rs.getString("fotoprofilo"), rs.getString("matricola"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}