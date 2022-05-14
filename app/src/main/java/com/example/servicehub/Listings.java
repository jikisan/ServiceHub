package com.example.servicehub;

public class Listings {



    String userID;
    String imageUrl;
    String imageName;
    String listName;
    String latitude;
    String longitude;
    String listAddress;
    String listPrice;
    String listQuantity;
    String listDesc;
    String ratings;

    public Listings(){ }

    public Listings(String userID, String imageUrl, String imageName, String listName, String latitude, String longitude, String listAddress, String listPrice,
                    String listQuantity, String listDesc, String ratings) {

        this.userID = userID;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.listName = listName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listAddress = listAddress;
        this.listPrice = listPrice;
        this.listQuantity = listQuantity;
        this.listDesc = listDesc;
        this.ratings = ratings;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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


    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
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


    public String getListAddress() {
        return listAddress;
    }

    public void setListAddress(String listAddress) {
        this.listAddress = listAddress;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getListQuantity() {
        return listQuantity;
    }

    public void setListQuantity(String listQuantity) {
        this.listQuantity = listQuantity;
    }

    public String getListDesc() {
        return listDesc;
    }

    public void setListDesc(String listDesc) {
        this.listDesc = listDesc;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }


}
