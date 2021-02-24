package com.example.chatapp.chat;

import com.firebase.geofire.GeoLocation;

import java.util.Date;

public class ChatMessages {
    private String messageText;
    private String messageUser;
    private String userName;
    private String userId;
    private double usrLocationLat;// = new double [2];
    private double usrLocationLong;
    private long msgTime;

    public ChatMessages(String messageText, String messageUser, String userName, String userIDD,  long time ) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userName = userName;
        this.userId = userIDD;

        // Initialize to current time
       this.msgTime = time;
    }

    public ChatMessages(){

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
    public String getUserName(){
        return userName;
    }
    public void setUserName(String uname){
        this.userName=uname;
    }
    public void setUserId(String userID){
        this.userId = userID;
    }
    public String getUserId(){
        return userId;
    }

}
