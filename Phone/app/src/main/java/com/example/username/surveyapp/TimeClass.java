package com.example.username.surveyapp;

/**
 * Created by Neil on 22/02/2017.
 */

public class TimeClass {
    private int hours;
    private int minutes;
    private int date;
    private int month;
    private long time;
    private int year;

    public long getTime() {return time;}

    public int getDate() {
        return date;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
