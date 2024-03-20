package it.uninaSocialGroup.DAO;

import it.uninaSocialGroup.Oggetti.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private Connection connection;

    public CommentDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM Comments WHERE post_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Comment comment = new Comment(
                            resultSet.getInt("post_id"),
                            resultSet.getString("username"),
                            resultSet.getString("content"),
                            resultSet.getTimestamp("timestamp"),
                            resultSet.getString("user_profile_picture")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    public void addComment(Comment comment) {
        String sql = "INSERT INTO Comments (content, post_id, username, user_profile_picture, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getPostId());
            stmt.setString(3, comment.getUsername());
            stmt.setString(4, comment.getUserProfilePicture());
            stmt.setTimestamp(5, comment.getTimestamp());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
