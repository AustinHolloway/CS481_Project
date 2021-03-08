package com.example.chatapp;

public class UserInfo {
    public String name, birthday, username;

    public UserInfo()
    {

    }

    public UserInfo(String name, String birthday, String username)
    {
        this.birthday = birthday;
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
