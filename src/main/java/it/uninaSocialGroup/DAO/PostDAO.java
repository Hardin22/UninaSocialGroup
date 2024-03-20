package it.uninaSocialGroup.DAO;

import it.uninaSocialGroup.Oggetti.Post;
import it.uninaSocialGroup.Utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    private Connection connection;

    public PostDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Post> getPostsByGroupId(int groupId, String currentUser) {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT Posts.*, UserLikes.Liked FROM Posts " +
                "LEFT JOIN UserLikes ON Posts.id = UserLikes.PostId AND UserLikes.nomeutente = ? " +
                "WHERE Posts.group_id = ? ORDER BY Posts.timestamp";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currentUser);
            statement.setInt(2, groupId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("author_username"),
                            resultSet.getString("content"),
                            resultSet.getString("postPicture"),
                            resultSet.getString("user_profile_picture"),
                            resultSet.getTimestamp("timestamp"),
                            resultSet.getInt("group_id"),
                            resultSet.getInt("numero_like"),
                            resultSet.getBoolean("Liked")
                    );
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    public void addPost(Post post) {
        String sql = "INSERT INTO Posts (content, postpicture, author_username, user_profile_picture, timestamp, group_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, post.getContent());
            stmt.setString(2, post.getPostPicture());
            stmt.setString(3, post.getAuthorUsername());
            stmt.setString(4, post.getUserProfilePicture());
            stmt.setTimestamp(5, post.getTimestamp());
            stmt.setInt(6, post.getGroupId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
