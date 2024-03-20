package it.uninaSocialGroup.Oggetti;

import java.sql.Time;
import java.sql.Timestamp;

public class Group {
    private String groupName;
    private String description;

    private String category;
    private int id;
    private String admin;
    private Timestamp creationDate;
    private String groupPictureLink;
    private int partecipants;
    public Group(String groupName, String description, String category, int id, String admin, Timestamp creationDate, String groupPictureLink) {
        this.groupName= groupName;
        this.description = description;
        this.category = category;
        this.id = id;
        this.admin = admin;
        this.creationDate = creationDate;
        this.groupPictureLink = groupPictureLink;
    }
    public Group(int id, String groupName, String groupPictureLink, String description, String category) {
        this.id = id;
        this.groupName = groupName;
        this.groupPictureLink = groupPictureLink;
        this.description = description;
        this.category = category;
    }
    public Group(int id, String groupName, String groupPictureLink) {
        this.id = id;
        this.groupName = groupName;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setGroupPictureLink(String groupPictureLink) {
        this.groupPictureLink = groupPictureLink;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;

    }
    public int getPartecipants() {
        return partecipants;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

}
