package com.example.servicehub;

public class Seller_application {

    String firstName;
    String lastName;
    String validIdName;
    String validIdUrl;
    String selfieName;
    String selfieUrl;
    String phoneNumber;
    String userID;
    boolean isApproved;
    boolean isPending;

    Seller_application(){}

    public Seller_application(String firstName, String lastName, String validIdName, String validIdUrl, String selfieName, String selfieUrl, String phoneNumber, String userID, boolean isApproved, boolean isPending) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.validIdName = validIdName;
        this.validIdUrl = validIdUrl;
        this.selfieName = selfieName;
        this.selfieUrl = selfieUrl;
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
