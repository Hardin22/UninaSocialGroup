package it.uninaSocialGroup.Oggetti;

import java.sql.Timestamp;

public class Comment {
    private int postId;
    private String username;
    private String content;
    private Timestamp timestamp;
    private String userProfilePicture;

    public Comment(int postId, String username, String content, Timestamp timestamp, String userProfilePicture) {
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.userProfilePicture = userProfilePicture;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }
}