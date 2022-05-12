package com.example.servicehub;

public class Notification {

    String notifImage;
    String notifTitle;
    String notifMessage;
    String notifTimeCreated;
    String userID;



    public Notification() {}

    public Notification(String notifImage, String notifTitle, String notifMessage, String notifTimeCreated, String userID) {
        this.notifImage = notifImage;
        this.notifTitle = notifTitle;
        this.notifMessage = notifMessage;
        this.notifTimeCreated = notifTimeCreated;
        this.userID = userID;
    }

    public String getNotifImage() {
        return notifImage;
    }

    public void setNotifImage(String notifImage) {
        this.notifImage = notifImage;
    }

    public String getNotifTitle() {
        return notifTitle;
    }

    public void setNotifTitle(String notifTitle) {
        this.notifTitle = notifTitle;
    }

    public String getNotifMessage() {
        return notifMessage;
    }

    public void setNotifMessage(String notifMessage) {
        this.notifMessage = notifMessage;
    }

    public String getNotifTimeCreated() {
        return notifTimeCreated;
    }

    public void setNotifTimeCreated(String notifTimeCreated) {
        this.notifTimeCreated = notifTimeCreated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
