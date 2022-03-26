package com.example.servicehub;

public class Projects {

    String projName;
    String projAddress;
    String price;
    String projTimeSlot;
    String projTimeSlotCount;
    String projInstruction;
    String userID;

    public Projects(){}

    public Projects(String projName, String projAddress, String price, String projTimeSlot, String projTimeSlotCount, String projInstruction, String userID) {
        this.projName = projName;
        this.projAddress = projAddress;
        this.price = price;
        this.projTimeSlot = projTimeSlot;
        this.projTimeSlotCount = projTimeSlotCount;
        this.projInstruction = projInstruction;
        this.userID = userID;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjAddress() {
        return projAddress;
    }

    public void setProjAddress(String projAddress) {
        this.projAddress = projAddress;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProjTimeSlot() {
        return projTimeSlot;
    }

    public void setProjTimeSlot(String projTimeSlot) {
        this.projTimeSlot = projTimeSlot;
    }

    public String getProjTimeSlotCount() {
        return projTimeSlotCount;
    }

    public void setProjTimeSlotCount(String projTimeSlotCount) {
        this.projTimeSlotCount = projTimeSlotCount;
    }

    public String getProjInstruction() {
        return projInstruction;
    }

    public void setProjInstruction(String projInstruction) {
        this.projInstruction = projInstruction;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
