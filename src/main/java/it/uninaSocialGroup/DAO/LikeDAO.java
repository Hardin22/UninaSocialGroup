package it.uninaSocialGroup.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LikeDAO {
    private Connection connection;

    public LikeDAO(Connection connection) {
        this.connection = connection;
    }

    public void updateLike(int postId, String userName, boolean isLiked) {
        // Gestione del like/dislike
        try {
            if (isLiked) {
                try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO UserLikes (Liked, PostId, nomeutente) VALUES (?, ?, ?)")) {
                    stmt.setBoolean(1, true);
                    stmt.setInt(2, postId);
                    stmt.setString(3, userName);
                    stmt.executeUpdate();
                }
            } else {
                try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM UserLikes WHERE PostId = ? AND nomeutente = ?")) {
                    stmt.setInt(1, postId);
                    stmt.setString(2, userName);
                    stmt.executeUpdate();
                }
            }
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE Posts SET numero_like = numero_like + ? WHERE id = ?")) {
                stmt.setInt(1, isLiked ? 1 : -1);
                stmt.setInt(2, postId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
