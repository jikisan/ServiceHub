package com.example.servicehub;

public class Projects {

    String userID;
    String category;
    String imageUrl;
    String imageName;
    String projName;
    String latitude;
    String longitude;
    String projAddress;
    String price;
    String percentageFee;
    String startTime;
    String endTime;
    String projInstruction;
    String ratings;
    boolean isAvailableMon;
    boolean isAvailableTue;
    boolean isAvailableWed;
    boolean isAvailableThu;
    boolean isAvailableFri;
    boolean isAvailableSat;
    boolean isAvailableSun;


    Projects(){

    }

    public Projects(String userID, String category, String imageUrl, String imageName, String projName, String latitude, String longitude, String projAddress, String price,
                    String percentageFee, String startTime, String endTime, String projInstruction, String ratings, boolean isAvailableMon, boolean isAvailableTue,
                    boolean isAvailableWed, boolean isAvailableThu, boolean isAvailableFri, boolean isAvailableSat, boolean isAvailableSun)
    {
        this.userID = userID;
        this.category = category;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.projName = projName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.projAddress = projAddress;
        this.price = price;
        this.percentageFee = percentageFee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projInstruction = projInstruction;
        this.ratings = ratings;
        this.isAvailableMon = isAvailableMon;
        this.isAvailableTue = isAvailableTue;
        this.isAvailableWed = isAvailableWed;
        this.isAvailableThu = isAvailableThu;
        this.isAvailableFri = isAvailableFri;
        this.isAvailableSat = isAvailableSat;
        this.isAvailableSun = isAvailableSun;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getPercentageFee() {
        return percentageFee;
    }

    public void setPercentageFee(String percentageFee) {
        this.percentageFee = percentageFee;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setProjTimeSlot(String endTime) {
        this.endTime = endTime;
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

    public boolean isAvailableMon() {
        return isAvailableMon;
    }

    public void setAvailableMon(boolean availableMon) {
        isAvailableMon = availableMon;
    }

    public boolean isAvailableTue() {
        return isAvailableTue;
    }

    public void setAvailableTue(boolean availableTue) {
        isAvailableTue = availableTue;
    }

    public boolean isAvailableWed() {
        return isAvailableWed;
    }

    public void setAvailableWed(boolean availableWed) {
        isAvailableWed = availableWed;
    }

    public boolean isAvailableThu() {
        return isAvailableThu;
    }

    public void setAvailableThu(boolean availableThu) {
        isAvailableThu = availableThu;
    }

    public boolean isAvailableFri() {
        return isAvailableFri;
    }

    public void setAvailableFri(boolean availableFri) {
        isAvailableFri = availableFri;
    }

    public boolean isAvailableSat() {
        return isAvailableSat;
    }

    public void setAvailableSat(boolean availableSat) {
        isAvailableSat = availableSat;
    }

    public boolean isAvailableSun() {
        return isAvailableSun;
    }

    public void setAvailableSun(boolean availableSun) {
        isAvailableSun = availableSun;
    }



}
