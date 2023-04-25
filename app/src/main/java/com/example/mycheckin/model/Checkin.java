package com.example.mycheckin.model;

import java.util.Objects;

public class Checkin {
    public Checkin filterDate(Checkin checkin, String date){
        if (Objects.equals(checkin.date, date)) return checkin;
        return null;
    }
    private String date, nameUser, timeCheckIn, timeCheckout, email;
    private int type, status;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getTimeCheckIn() {
        return timeCheckIn;
    }

    public void setTimeCheckIn(String timeCheckIn) {
        this.timeCheckIn = timeCheckIn;
    }

    public String getTimeCheckout() {
        return timeCheckout;
    }

    public void setTimeCheckout(String timeCheckout) {
        this.timeCheckout = timeCheckout;
    }

    public String getEmaill() {
        return email;
    }

    public void setEmaill(String emaill) {
        this.email = emaill;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Checkin() {
    }

    public Checkin(String date, String nameUser, String timeCheckIn, String timeCheckout, String email, int type, int status) {
        this.date = date;
        this.nameUser = nameUser;
        this.timeCheckIn = timeCheckIn;
        this.timeCheckout = timeCheckout;
        this.email = email;
        this.type = type;
        this.status = status;
    }
}
