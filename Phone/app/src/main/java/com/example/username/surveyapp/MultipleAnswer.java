package com.example.username.surveyapp;

import java.security.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Neil on 15/01/2017.
 */

public class MultipleAnswer {
    private String user;
    private String answer;
    private Date timeStamp;
    private long responseTime;

    public MultipleAnswer(){}

    public MultipleAnswer(String user, String answer, Date timeStamp, long responseTime) {
        this.user = user;
        this.answer = answer;
        this.timeStamp = timeStamp;
        this.responseTime = responseTime;
    }
    public String getUser() {return user;}

    public String getAnswer() {
        return answer;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setUser(String id) {
        this.user = id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
