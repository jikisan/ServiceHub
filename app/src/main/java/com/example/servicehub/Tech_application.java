package com.example.servicehub;

public class Tech_application {

    String firstName;
    String lastName;
    String validIdName;
    String validIdUrl;
    String selfieName;
    String selfieUrl;
    String fileName;
    String fileUrl;
    String phoneNumber;
    String userID;
    boolean isApproved;
    boolean isPending;

    Tech_application(){

    }

    public Tech_application(String firstName, String lastName, String validIdName, String validIdUrl, String selfieName,
                            String selfieUrl, String fileName, String fileUrl, String phoneNumber, String userID,
                            boolean isApproved, boolean isPending)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.validIdName = validIdName;
        this.validIdUrl = validIdUrl;
        this.selfieName = selfieName;
        this.selfieUrl = selfieUrl;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.phoneNumber = phoneNumber;
        this.userID = userID;
        this.isApproved = isApproved;
        this.isPending = isPending;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getValidIdName() {
        return validIdName;
    }

    public void setValidIdName(String validIdName) {
        this.validIdName = validIdName;
    }

    public String getValidIdUrl() {
        return validIdUrl;
    }

    public void setValidIdUrl(String validIdUrl) {
        this.validIdUrl = validIdUrl;
    }

    public String getSelfieName() {
        return selfieName;
    }

    public void setSelfieName(String selfieName) {
        this.selfieName = selfieName;
    }

    public String getSelfieUrl() {
        return selfieUrl;
    }

    public void setSelfieUrl(String selfieUrl) {
        this.selfieUrl = selfieUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
