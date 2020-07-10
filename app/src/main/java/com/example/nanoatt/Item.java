package com.example.nanoatt;

public class Item {
    String user;
    String date;
    String time_in;
    String time_out;

    public Item(String user, String date, String time_in, String time_out) {
        this.user = user;
        this.date = date;
        this.time_in = time_in;
        this.time_out = time_out;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }
}
