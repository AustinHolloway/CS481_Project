package com.example.chatapp.chat;

public class RegionalChatMessages {

    private String messageText;
    private String messageUser;
    private String userName;
    private String userId;
    private double usrLocationLat;// = new double [2];
    private double usrLocationLong;
    private long msgTime;

    public RegionalChatMessages(String messageText, String messageUser, String userName, String userIDD, double[] currLocation, long time) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userName = userName;
        this.usrLocationLat = currLocation[0]; //latitude
        this.usrLocationLong = currLocation[1]; //longitude
        this.userId = userIDD;

        // Initialize to current time
        this.msgTime = time;//new Date().getTime();

    }

    public RegionalChatMessages() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long messageTime) {
        this.msgTime = messageTime;
    }

    public String getUserName() { return userName; }

    public void setUserName(String uname) { this.userName = uname; }

    public void setUserId(String userID) { this.userId = userID; }

    public String getUserId() { return userId; }

    public double getUsrLocationLat() { return usrLocationLat; }

    public void setUsrLocationLat(double lat) { this.usrLocationLat = lat; }

    public double getUsrLocationLong() { return usrLocationLong; }

    public void setUsrLocationLong(double lng) { this.usrLocationLong = lng; }
}


