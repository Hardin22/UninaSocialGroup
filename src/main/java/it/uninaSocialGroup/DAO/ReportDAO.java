package it.uninaSocialGroup.DAO;

import it.uninaSocialGroup.Oggetti.Group;
import it.uninaSocialGroup.Oggetti.MonthlyReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection connection;

    public ReportDAO(Connection connection) {
        this.connection = connection;
    }



    public String getMostLikedPost(String groupName, String month) {
        String mostLikedPostContent = null;
        String sql = "SELECT p.content AS postContent " +
                "FROM posts p JOIN gruppi g ON p.group_id = g.id_gruppo " +
                "WHERE g.nome = ? AND to_char(p.timestamp, 'YYYY-MM') = ? " +
                "ORDER BY p.numero_like DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupName);
            stmt.setString(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mostLikedPostContent = rs.getString("postContent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mostLikedPostContent;
    }

    public String getLeastLikedPost(String groupName, String month) {
        String leastLikedPostContent = null;
        String sql = "SELECT p.content AS postContent " +
                "FROM posts p JOIN gruppi g ON p.group_id = g.id_gruppo " +
                "WHERE g.nome = ? AND to_char(p.timestamp, 'YYYY-MM') = ? " +
                "ORDER BY p.numero_like ASC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupName);
            stmt.setString(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    leastLikedPostContent = rs.getString("postContent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leastLikedPostContent;
    }

    public List<String> getAdminGroups(String admin, String month) {
        List<String> groupNames = new ArrayList<>();
        String sql = "SELECT DISTINCT g.nome " +
                "FROM gruppi g " +
                "JOIN posts p ON g.id_gruppo = p.group_id " +
                "WHERE g.nomeCreatore = ? AND to_char(p.timestamp, 'YYYY-MM') = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin);
            stmt.setString(2, month);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    groupNames.add(nome);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return groupNames;
    }

    public String getMostCommentedPost(String groupName, String month) {
        String mostCommentedPostContent = null;
        String sql = "WITH CommentCounts AS (" +
                "    SELECT p.id, p.group_id, p.content AS postContent, COUNT(c.id) AS commentCount " +
                "    FROM posts p " +
                "    LEFT JOIN comments c ON p.id = c.post_id " +
                "    JOIN gruppi g ON p.group_id = g.id_gruppo " +
                "    WHERE to_char(p.timestamp, 'YYYY-MM') = ? AND g.nome = ? " +
                "    GROUP BY p.id, p.group_id " +
                ") " +
                "SELECT cc.postContent " +
                "FROM CommentCounts cc " +
                "WHERE cc.commentCount = (" +
                "    SELECT MAX(commentCount) " +
                "    FROM CommentCounts" +
                ") " +
                "LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, month);
            stmt.setString(2, groupName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mostCommentedPostContent = rs.getString("postContent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mostCommentedPostContent;
    }

    public String getLeastCommentedPost(String groupName, String month) {
        String leastCommentedPostContent = null;
        String sql = "WITH CommentCounts AS (" +
                "    SELECT p.id, p.group_id, p.content AS postContent, COUNT(c.id) AS commentCount " +
                "    FROM posts p " +
                "    LEFT JOIN comments c ON p.id = c.post_id " +
                "    JOIN gruppi g ON p.group_id = g.id_gruppo " +
                "    WHERE to_char(p.timestamp, 'YYYY-MM') = ? AND g.nome = ? " +
                "    GROUP BY p.id, p.group_id " +
                "), MinComments AS (" +
                "    SELECT MIN(cc.commentCount) AS minCommentCount " +
                "    FROM CommentCounts cc " +
                ") " +
                "SELECT cc.postContent " +
                "FROM CommentCounts cc " +
                "WHERE cc.commentCount = (SELECT minCommentCount FROM MinComments) " +
                "LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, month);
            stmt.setString(2, groupName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    leastCommentedPostContent = rs.getString("postContent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leastCommentedPostContent;
    }

    public double getAveragePostsPerGroup(String groupName, String month) {
        double averagePostCount = 0;
        String sql = "SELECT AVG(p.postCount) AS averagePostCount " +
                "FROM (" +
                "    SELECT group_id, COUNT(*) AS postCount " +
                "    FROM posts " +
                "    WHERE to_char(timestamp, 'YYYY-MM') = ? " +
                "    GROUP BY group_id" +
                ") AS p " +
                "JOIN gruppi g ON p.group_id = g.id_gruppo " +
                "WHERE g.nome = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, month);
            stmt.setString(2, groupName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    averagePostCount = rs.getDouble("averagePostCount");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return averagePostCount;
    }

}
