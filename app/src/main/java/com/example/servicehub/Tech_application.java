package com.example.servicehub;

public class Tech_application {

    String firstName;
    String lastName;
    String validIdName;
    String validIdUrl;
    String selfieName;
    String selfieUrl;
    String proofOfWorkName;
    String proofOfWorkUrl;
    String proofOfEquipmentName;
    String proofOfEquipmentUrl;
    String phoneNumber;
    String userID;
    boolean isApproved;
    boolean isPending;

    Tech_application(){

    }

    public Tech_application(String firstName, String lastName, String validIdName, String validIdUrl, String selfieName,
                            String selfieUrl, String proofOfWorkName, String proofOfWorkUrl, String proofOfEquipmentName,
                            String proofOfEquipmentUrl, String phoneNumber, String userID, boolean isApproved,
                            boolean isPending)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.validIdName = validIdName;
        this.validIdUrl = validIdUrl;
        this.selfieName = selfieName;
        this.selfieUrl = selfieUrl;
        this.proofOfWorkName = proofOfWorkName;
        this.proofOfWorkUrl = proofOfWorkUrl;
        this.proofOfEquipmentName = proofOfEquipmentName;
        this.proofOfEquipmentUrl = proofOfEquipmentUrl;
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

    public String getProofOfWorkName() {
        return proofOfWorkName;
    }

    public void setProofOfWorkName(String proofOfWorkName) {
        this.proofOfWorkName = proofOfWorkName;
    }

    public String getProofOfWorkUrl() {
        return proofOfWorkUrl;
    }

    public void setProofOfWorkUrl(String proofOfWorkUrl) {
        this.proofOfWorkUrl = proofOfWorkUrl;
    }

    public String getProofOfEquipmentName() {
        return proofOfEquipmentName;
    }

    public void setProofOfEquipmentName(String proofOfEquipmentName) {
        this.proofOfEquipmentName = proofOfEquipmentName;
    }

    public String getProofOfEquipmentUrl() {
        return proofOfEquipmentUrl;
    }

    public void setProofOfEquipmentUrl(String proofOfEquipmentUrl) {
        this.proofOfEquipmentUrl = proofOfEquipmentUrl;
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
