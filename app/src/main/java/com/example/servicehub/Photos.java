package com.example.servicehub;

public class Photos {

    String projID;
    String link;

    public Photos() {
    }

    public Photos(String projID, String link) {
        this.projID = projID;
        this.link = link;
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
}
