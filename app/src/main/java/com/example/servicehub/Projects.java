package com.example.servicehub;

public class Projects {

    String imageUrl;
    String imageName;
    String projName;
    String projLatLng;
    String projAddress;
    String price;
    String projTimeSlot;
    String projInstruction;
    String ratings;

    Projects(){

    }

    public Projects(String imageUrl, String imageName, String projName, String projLatLng, String projAddress, String price, String projTimeSlot, String projInstruction, String ratings) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.projName = projName;
        this.projLatLng = projLatLng;
        this.projAddress = projAddress;
        this.price = price;
        this.projTimeSlot = projTimeSlot;
        this.projInstruction = projInstruction;
        this.ratings = ratings;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }


    public String getProjLatLng() {
        return projLatLng;
    }

    public void setProjLatLng(String projLatLng) {
        this.projLatLng = projLatLng;
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

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }


}
