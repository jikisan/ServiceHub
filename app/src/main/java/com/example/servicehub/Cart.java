package com.example.servicehub;

public class Cart {

    String custID;
    String listingID;
    String dateAdded;

    public Cart() {
    }

    public Cart(String custID, String listingID, String dateAdded) {
        this.custID = custID;
        this.listingID = listingID;
        this.dateAdded = dateAdded;
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
}
