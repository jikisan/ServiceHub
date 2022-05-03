package com.example.servicehub;

public class Favorites {

    String custID;
    String projID;
    String dateAdded;

    public Favorites(String custID, String projID, String dateAdded) {
        this.custID = custID;
        this.projID = projID;
        this.dateAdded = dateAdded;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getProjID() {
        return projID;
    }

    public void setProjID(String projID) {
        this.projID = projID;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}
