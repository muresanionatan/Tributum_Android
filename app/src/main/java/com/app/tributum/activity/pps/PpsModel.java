package com.app.tributum.activity.pps;

import java.util.List;

public class PpsModel {

    private String name;

    private String momName;

    private String address;

    private String email;

    private String phone;

    private String message;

    public PpsModel(String name,
                    String momName,
                    String address,
                    String email,
                    String phone) {
        this.name = name;
        this.momName = momName;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getMomName() {
        return momName;
    }

    public void setMomName(String momName) {
        this.momName = momName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }
}