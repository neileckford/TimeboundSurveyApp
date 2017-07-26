package com.example.username.surveyapp;

import java.util.List;
import java.util.Map;

/**
 * Created by Neil on 11/01/2017.
 */

public class MultipleChoice {
    private String quesID;
    private String question;
    private List<String> options;

    public MultipleChoice() {
    }

    public String getQuesID() {
        return quesID;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setQuesID(String quesID) {
        this.quesID = quesID;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
