package com.example.servicehub;

public class Orders {

    String custID;
    String listingID;
    String sellerID;
    String custName;
    String custContactNum;
    String custDeliveryAddress;
    String latlng;
    String imageUrl;
    String itemName;
    String message;
    String prodSubTotal;
    String shipFeeSubTotal;
    String totalPayment;

    public Orders() {
    }

    public Orders(String custID, String listingID, String sellerID, String custName, String custContactNum, String custDeliveryAddress,
                  String latlng, String imageUrl, String itemName, String message, String prodSubTotal,
                  String shipFeeSubTotal, String totalPayment) {
        this.custID = custID;
        this.listingID = listingID;
        this.sellerID = sellerID;
        this.custName = custName;
        this.custContactNum = custContactNum;
        this.custDeliveryAddress = custDeliveryAddress;
        this.latlng = latlng;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.message = message;
        this.prodSubTotal = prodSubTotal;
        this.shipFeeSubTotal = shipFeeSubTotal;
        this.totalPayment = totalPayment;
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

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustContactNum() {
        return custContactNum;
    }

    public void setCustContactNum(String custContactNum) {
        this.custContactNum = custContactNum;
    }

    public String getCustDeliveryAddress() {
        return custDeliveryAddress;
    }

    public void setCustDeliveryAddress(String custDeliveryAddress) {
        this.custDeliveryAddress = custDeliveryAddress;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProdSubTotal() {
        return prodSubTotal;
    }

    public void setProdSubTotal(String prodSubTotal) {
        this.prodSubTotal = prodSubTotal;
    }

    public String getShipFeeSubTotal() {
        return shipFeeSubTotal;
    }

    public void setShipFeeSubTotal(String shipFeeSubTotal) {
        this.shipFeeSubTotal = shipFeeSubTotal;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment = totalPayment;
    }
}
