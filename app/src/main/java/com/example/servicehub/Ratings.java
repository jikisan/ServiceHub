package com.example.servicehub;

public class Ratings {

    String ratingOfId;
    String ratingOfFirstName;
    String ratingOfLastName;
    String ratingById;
    String ratingByName;
    double ratingValue;
    String ratingMessage;

    public Ratings() {
    }

    public Ratings(String ratingOfId, String ratingOfFirstName, String ratingOfLastName, String ratingById,
                   String ratingByName, double ratingValue, String ratingMessage) {
        this.ratingOfId = ratingOfId;
        this.ratingOfFirstName = ratingOfFirstName;
        this.ratingOfLastName = ratingOfLastName;
        this.ratingById = ratingById;
        this.ratingByName = ratingByName;
        this.ratingValue = ratingValue;
        this.ratingMessage = ratingMessage;
    }

    public String getRatingOfId() {
        return ratingOfId;
    }

    public void setRatingOfId(String ratingOfId) {
        this.ratingOfId = ratingOfId;
    }

    public String getRatingOfFirstName() {
        return ratingOfFirstName;
    }

    public void setRatingOfFirstName(String ratingOfFirstName) {
        this.ratingOfFirstName = ratingOfFirstName;
    }

    public String getRatingOfLastName() {
        return ratingOfLastName;
    }

    public void setRatingOfLastName(String ratingOfLastName) {
        this.ratingOfLastName = ratingOfLastName;
    }

    public String getRatingById() {
        return ratingById;
    }

    public void setRatingById(String ratingById) {
        this.ratingById = ratingById;
    }

    public String getRatingByName() {
        return ratingByName;
    }

    public void setRatingByName(String ratingByName) {
        this.ratingByName = ratingByName;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }
}
