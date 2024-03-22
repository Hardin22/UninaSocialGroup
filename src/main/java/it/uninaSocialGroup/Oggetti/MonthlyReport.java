package it.uninaSocialGroup.Oggetti;

public class MonthlyReport {
    private String groupName;
    private String mostLikedPostContent;
    private String leastLikedPostContent;
    private String mostCommentedPostContent;
    private String leastCommentedPostContent;
    private String averagePostsPerGroup; // Cambiato da double a String

    // Costruttori, getter e setter

    public MonthlyReport() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMostLikedPostContent() {
        return mostLikedPostContent;
    }

    public void setMostLikedPostContent(String mostLikedPostContent) {
        this.mostLikedPostContent = mostLikedPostContent;
    }

    public String getLeastLikedPostContent() {
        return leastLikedPostContent;
    }

    public void setLeastLikedPostContent(String leastLikedPostContent) {
        this.leastLikedPostContent = leastLikedPostContent;
    }

    public String getMostCommentedPostContent() {
        return mostCommentedPostContent;
    }

    public void setMostCommentedPostContent(String mostCommentedPostContent) {
        this.mostCommentedPostContent = mostCommentedPostContent;
    }

    public String getLeastCommentedPostContent() {
        return leastCommentedPostContent;
    }

    public void setLeastCommentedPostContent(String leastCommentedPostContent) {
        this.leastCommentedPostContent = leastCommentedPostContent;
    }

    public String getAveragePostsPerGroup() {
        return averagePostsPerGroup;
    }

    // Nota: il metodo è cambiato per accettare una Stringa come parametro
    public void setAveragePostsPerGroup(String averagePostsPerGroup) {
        this.averagePostsPerGroup = averagePostsPerGroup;
    }
}
