package com.example.servicehub;

public class Favorites {

    String custID;
    String projID;
    String dateAdded;
    String projImageUrl;
    String projName;
    String projPrice;
    String projRatings;



    public Favorites() {

    }

    public Favorites(String custID, String projID, String dateAdded, String projImageUrl, String projName, String projPrice, String projRatings) {
        this.custID = custID;
        this.projID = projID;
        this.dateAdded = dateAdded;
        this.projImageUrl = projImageUrl;
        this.projName = projName;
        this.projPrice = projPrice;
        this.projRatings = projRatings;
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

    public String getProjImageUrl() {
        return projImageUrl;
    }

    public void setProjImageUrl(String projImageUrl) {
        this.projImageUrl = projImageUrl;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getProjPrice() {
        return projPrice;
    }

    public void setProjPrice(String projPrice) {
        this.projPrice = projPrice;
    }

    public String getProjRatings() {
        return projRatings;
    }

    public void setProjRatings(String projRatings) {
        this.projRatings = projRatings;
    }
}
