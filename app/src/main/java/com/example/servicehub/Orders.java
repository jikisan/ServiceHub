package com.example.servicehub;

public class Orders {

    String custID;
    String listingID;
    String sellerID;
    String custName;
    String custContactNum;
    String custDeliveryAddress;
    String latitude;
    String longitude;
    String imageUrl;
    String itemName;
    String itemQuantity;
    String message;
    String prodSubTotal;
    String shipFeeSubTotal;
    String totalPayment;
    String paymentMethod;
    String proofOfPaymentUrl;

    public Orders() {
    }

    public Orders(String custID, String listingID, String sellerID, String custName, String custContactNum,
                  String custDeliveryAddress, String latitude, String longitude, String imageUrl,
                  String itemName, String itemQuantity, String message, String prodSubTotal,
                  String shipFeeSubTotal, String totalPayment, String paymentMethod,
                  String proofOfPaymentUrl) {
        this.custID = custID;
        this.listingID = listingID;
        this.sellerID = sellerID;
        this.custName = custName;
        this.custContactNum = custContactNum;
        this.custDeliveryAddress = custDeliveryAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.message = message;
        this.prodSubTotal = prodSubTotal;
        this.shipFeeSubTotal = shipFeeSubTotal;
        this.totalPayment = totalPayment;
        this.paymentMethod = paymentMethod;
        this.proofOfPaymentUrl = proofOfPaymentUrl;
    }
    {
        this.custID = custID;
        this.listingID = listingID;
        this.sellerID = sellerID;
        this.custName = custName;
        this.custContactNum = custContactNum;
        this.custDeliveryAddress = custDeliveryAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.message = message;
        this.prodSubTotal = prodSubTotal;
        this.shipFeeSubTotal = shipFeeSubTotal;
        this.totalPayment = totalPayment;
        this.paymentMethod = paymentMethod;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
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
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProofOfPaymentUrl() {
        return proofOfPaymentUrl;
    }

    public void setProofOfPaymentUrl(String proofOfPaymentUrl) {
        this.proofOfPaymentUrl = proofOfPaymentUrl;
    }
}
