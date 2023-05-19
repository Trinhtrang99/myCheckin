package com.example.mycheckin.model;

public class User {
    private String name;
    private String mail;
    private String url;

    public String getUrl() {
        return url;
    }

    public User(String name, String mail, String url) {
        this.name = name;
        this.mail = mail;
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
