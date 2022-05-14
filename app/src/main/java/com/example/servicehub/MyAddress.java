package com.example.servicehub;

public class MyAddress {

    String addressLabel;
    String addressValue;
    String custID;
    String longString;
    String latString;

    public MyAddress() {
    }

    public MyAddress(String addressLabel, String addressValue, String custID, String longString, String latString) {
        this.addressLabel = addressLabel;
        this.addressValue = addressValue;
        this.custID = custID;
        this.longString = longString;
        this.latString = latString;
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

    public String getLongString() {
        return longString;
    }

    public void setLongString(String longString) {
        this.longString = longString;
    }

    public String getLatString() {
        return latString;
    }

    public void setLatString(String latString) {
        this.latString = latString;
    }

}
