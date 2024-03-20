package it.uninaSocialGroup.Oggetti;
import java.sql.Timestamp;

public class Post {
    private int id;
    private String authorUsername;
    private String content;
    private String postPicture;
    private String userProfilePicture;
    private Timestamp timestamp;
    private int groupId;
    private int likeNumber;
    private boolean liked;


    public Post(int id, String authorUsername, String content, String postPicture, String userProfilePicture, Timestamp timestamp,int groupId, int likeNumber, boolean liked) {
        this.id = id;
        this.authorUsername = authorUsername;
        this.content = content;
        this.postPicture = postPicture;
        this.userProfilePicture = userProfilePicture;
        this.timestamp = timestamp;
        this.groupId = groupId;
        this.likeNumber = likeNumber;
        this.liked = liked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(String postPicture) {
        this.postPicture = postPicture;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}