package com.app.tributum.activity.company.model;

public class Secretary {

    private String firstName;
    private String surName;
    private String birthday;
    private String email;
    private String pps;
    private String nationality;
    private String address;

    public Secretary(String firstName, String surName, String birthday, String email, String pps, String nationality, String address) {
        this.firstName = firstName;
        this.surName = surName;
        this.birthday = birthday;
        this.email = email;
        this.pps = pps;
        this.nationality = nationality;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPps() {
        return pps;
    }

    public void setPps(String pps) {
        this.pps = pps;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
