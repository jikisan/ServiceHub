package com.example.servicehub;

public class Wallets {

    String userID;
    Double fundAmount;

    public Wallets() {
    }

    public Wallets(String userID, Double fundAmount) {
        this.userID = userID;
        this.fundAmount = fundAmount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Double getFundAmount() {
        return fundAmount;
    }

    public void setFundAmount(Double fundAmount) {
        this.fundAmount = fundAmount;
    }
}
