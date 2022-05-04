package com.example.servicehub;

public class Cart {

    String custID;
    String listingID;
    String dateAdded;
    String listImageUrl;
    String listName;
    String listPrice;
    String listRatings;

    public Cart() {
    }

    public Cart(String custID, String listingID, String dateAdded, String listImageUrl, String listName, String listPrice, String listRatings) {
        this.custID = custID;
        this.listingID = listingID;
        this.dateAdded = dateAdded;
        this.listImageUrl = listImageUrl;
        this.listName = listName;
        this.listPrice = listPrice;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getListingID() {
        return listingID;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getListImageUrl() {
        return listImageUrl;
    }

    public void setListImageUrl(String listImageUrl) {
        this.listImageUrl = listImageUrl;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getListRatings() {
        return listRatings;
    }

    public void setListRatings(String listRatings) {
        this.listRatings = listRatings;
    }
}
