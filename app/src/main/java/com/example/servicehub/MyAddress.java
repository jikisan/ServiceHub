package com.example.servicehub;

public class MyAddress {

    String addressLabel;
    String addressValue;
    String custID;

    public MyAddress() {
    }

    public MyAddress(String addressLabel, String addressValue, String custID) {
        this.addressLabel = addressLabel;
        this.addressValue = addressValue;
        this.custID = custID;
    }

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public String getAddressValue() {
        return addressValue;
    }

    public void setAddressValue(String addressValue) {
        this.addressValue = addressValue;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

}
