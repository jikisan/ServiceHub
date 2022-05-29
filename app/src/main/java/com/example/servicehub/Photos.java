package com.example.servicehub;

public class Photos {

    String projID;
    String link;
    String photoName;

    public Photos() {
    }

    public Photos(String projID, String link, String photoName) {
        this.projID = projID;
        this.link = link;
        this.photoName = photoName;
    }

    public String getProjID() {
        return projID;
    }

    public void setProjID(String projID) {
        this.projID = projID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
