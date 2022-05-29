package com.example.servicehub;

public class Videos {

    String projID;
    String link;
    String videoName;

    public Videos() {
    }

    public Videos(String projID, String link, String videoName) {
        this.projID = projID;
        this.link = link;
        this.videoName = videoName;
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

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
