package com.example.mycheckin.model;

public class UsersModel {
    private  String birthday, email,name,phone,position;
    private Checkin checkin;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmaul() {
        return email;
    }

    public void setEmaul(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Checkin getCheckin() {
        return checkin;
    }

    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }

    public UsersModel() {

    }

    public UsersModel(String birthday, String emaul, String name, String phone, String position, Checkin checkin) {
        this.birthday = birthday;
        this.email = emaul;
        this.name = name;
        this.phone = phone;
        this.position = position;
        this.checkin = checkin;
    }
}
