package com.app.tributum.activity.company.model;

public class Company {

    private String firstName;
    private String surName;
    private String email;
    private String phone;
    private String companyName;
    private String proposedCompanyName;
    private String address;
    private String town;
    private String country;
    private String activities;
    private String registeredOffice;

    public Company(String firstName, String surName, String email, String phone, String companyName, String proposedCompanyName, String address, String town, String country, String activities, String registeredOffice) {
        this.firstName = firstName;
        this.surName = surName;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.proposedCompanyName = proposedCompanyName;
        this.address = address;
        this.town = town;
        this.country = country;
        this.activities = activities;
        this.registeredOffice = registeredOffice;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProposedCompanyName() {
        return proposedCompanyName;
    }

    public void setProposedCompanyName(String proposedCompanyName) {
        this.proposedCompanyName = proposedCompanyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getRegisteredOffice() {
        return registeredOffice;
    }

    public void setRegisteredOffice(String registeredOffice) {
        this.registeredOffice = registeredOffice;
    }
}
