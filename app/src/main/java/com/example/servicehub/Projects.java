package com.example.servicehub;

import android.net.Uri;

public class Projects {

    String imageUri;
    String projName;
    String projAddress;
    String price;
    String projTimeSlot;
    String projInstruction;
    String userID;
    String latLng;

    public Projects(){

    }

    public Projects(String imageUri, String projName, String projAddress, String price, String projTimeSlot, String projInstruction, String userID, String latLng) {
        this.imageUri = imageUri;
        this.projName = projName;
        this.projAddress = projAddress;
        this.price = price;
        this.projTimeSlot = projTimeSlot;
        this.projInstruction = projInstruction;
        this.userID = userID;
        this.latLng = latLng;
    }

    public String getResultUriText() {
        return imageUri;
    }

    public void setResultUriText(String imageUri) {
        this.imageUri = imageUri;
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

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }


}
