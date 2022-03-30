package com.example.servicehub;

public class Projects {

    String imageUri;
    String projName;
    String projAddress;
    String price;
    String projTimeSlot;
    String projInstruction;
    String latLng;

    private Projects(){

    }

    public Projects(String imageUri, String projName, String projAddress, String price, String projTimeSlot, String projInstruction, String latLng) {
        this.imageUri = imageUri;
        this.projName = projName;
        this.projAddress = projAddress;
        this.price = price;
        this.projTimeSlot = projTimeSlot;
        this.projInstruction = projInstruction;
        this.latLng = latLng;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
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


    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }


}
