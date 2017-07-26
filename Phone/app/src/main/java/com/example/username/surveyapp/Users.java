package com.example.username.surveyapp;

import java.util.Date;

/**
 * Created by Neil on 15/03/2017.
 */

public class Users {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public Users(String userName) {
        this.userName = userName;
    }

    public Users(){}
}
