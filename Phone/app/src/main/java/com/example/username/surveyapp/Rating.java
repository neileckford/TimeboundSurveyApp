package com.example.username.surveyapp;

import java.util.List;

/**
 * Created by Neil on 17/03/2017.
 */

public class Rating {
    private String question;
    private String stars;

    public Rating() {}

    public String getQuestion() {
        return question;
    }

    public String getStars() {
        return stars;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setStars(String stars) {this.stars = stars;}
}
