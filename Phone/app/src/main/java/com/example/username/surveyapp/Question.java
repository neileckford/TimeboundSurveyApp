package com.example.username.surveyapp;

import java.util.List;
import java.util.Map;

/**
 * Created by Neil on 08/02/2017.
 */

public class Question {
    private String quesID;
    private String type;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;
    private String question;

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    private String postedBy;
    private TimeClass timeStamp;
    private Deadline deadline;

    public Deadline getDeadline() {
        return deadline;
    }

    public TimeClass getTimeStamp() {
        return timeStamp;
    }

    public void setQuesID(String quesID) {
        this.quesID = quesID;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuesID() {
        return quesID;
    }
    public String getType() {
        return type;
    }
    public String getQuestion() {
        return question;
    }
}
