package com.app.tributum.activity.pps;

public class PpsModel {

    private String name;

    private String momName;

    private String address;

    private String email;

    private String phone;

    private String ownerPhone;

    private String message;

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public PpsModel(String name,
                    String momName,
                    String address,
                    String email,
                    String phone,
                    String ownerPhone) {
        this.name = name;
        this.momName = momName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.ownerPhone = ownerPhone;
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