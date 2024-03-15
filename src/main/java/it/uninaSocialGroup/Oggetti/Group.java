package it.uninaSocialGroup.Oggetti;

public class Group {
    private String groupName;
    private String description;
    private String id;
    private String admin;
    private String groupPictureLink;

    public Group(String groupName, String description, String id, String admin, String groupPictureLink) {
        this.groupName= groupName;
        this.description = description;
        this.id = id;
        this.admin = admin;
        this.groupPictureLink = groupPictureLink;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
    

    public String getGroupPictureLink() {
        return groupPictureLink;
    }

    public void setProfilePictureLink(String groupPictureLink) {
        this.groupPictureLink = groupPictureLink;
    }
}
