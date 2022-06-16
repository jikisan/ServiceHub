package com.example.servicehub;

public class Fund_Request {

    String userID;
    Double fundAmount;
    String proofOfPaymentName;
    String proofOfPaymentUrl;

    public Fund_Request() {
    }

    public Fund_Request(String userID, Double fundAmount, String proofOfPaymentName, String proofOfPaymentUrl) {
        this.userID = userID;
        this.fundAmount = fundAmount;
        this.proofOfPaymentName = proofOfPaymentName;
        this.proofOfPaymentUrl = proofOfPaymentUrl;
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

    public String getProofOfPaymentName() {
        return proofOfPaymentName;
    }

    public void setProofOfPaymentName(String proofOfPaymentName) {
        this.proofOfPaymentName = proofOfPaymentName;
    }

    public String getProofOfPaymentUrl() {
        return proofOfPaymentUrl;
    }

    public void setProofOfPaymentUrl(String proofOfPaymentUrl) {
        this.proofOfPaymentUrl = proofOfPaymentUrl;
    }
}
