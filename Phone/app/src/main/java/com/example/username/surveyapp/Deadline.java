package com.example.username.surveyapp;

/**
 * Created by Neil on 23/02/2017.
 */

public class Deadline {
    private int hours;
    private int minutes;
    private int date;
    private int month;
    private long time;
    private int year;

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public long getTime() {return time;}

    public int getYear() {
        return year;
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
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
