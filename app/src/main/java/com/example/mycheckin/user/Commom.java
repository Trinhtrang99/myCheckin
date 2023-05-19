package com.example.mycheckin.user;

public class Commom {
    String lat;
    String log;
    String wifi_ip;

    String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Commom() {

    }

    public Commom(String lat, String log, String wifi_ip) {
        this.lat = lat;
        this.log = log;
        this.wifi_ip = wifi_ip;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getWifi_ip() {
        return wifi_ip;
    }

    public void setWifi_ip(String wifi_ip) {
        this.wifi_ip = wifi_ip;
    }
}
