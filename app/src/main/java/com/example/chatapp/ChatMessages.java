package com.example.chatapp;

import com.firebase.geofire.GeoLocation;

import java.util.Date;

public class ChatMessages {
    private String msgText;
    private String msgUsr;
    private String usrName;
    private GeoLocation usrLocation;
    private long msgTime;

    public ChatMessages(String messageText, String messageUser, String userName, GeoLocation currLocation ) {
        this.msgText = messageText;
        this.msgUsr = messageUser;
        this.usrName = userName;
        this.usrLocation = currLocation;

        // Initialize to current time
        msgTime = System.currentTimeMillis();//new Date().getTime();
       // getUsrName();
    }

    public ChatMessages(){

    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String messageText) {
        this.msgText = messageText;
    }

    public String getMsgUsr() {
        return msgUsr;
    }

    public void setMsgUsr(String messageUser) {
        this.msgUsr = messageUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long messageTime) {
        this.msgTime = messageTime;
    }
    public String getUsrName(){
        return usrName;
    }
}
