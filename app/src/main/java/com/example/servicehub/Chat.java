package com.example.servicehub;

public class Chat {

    String senderUid;
    String senderPhotoUrl;
    String receiverUid;
    String receiverPhotoUrl;
    String receiverName;

    public Chat() {
    }

    public Chat(String senderUid, String senderPhotoUrl, String receiverUid,
                String receiverPhotoUrl, String receiverName) {
        this.senderUid = senderUid;
        this.senderPhotoUrl = senderPhotoUrl;
        this.receiverUid = receiverUid;
        this.receiverPhotoUrl = receiverPhotoUrl;
        this.receiverName = receiverName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getReceiverPhotoUrl() {
        return receiverPhotoUrl;
    }

    public void setReceiverPhotoUrl(String receiverPhotoUrl) {
        this.receiverPhotoUrl = receiverPhotoUrl;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


}
