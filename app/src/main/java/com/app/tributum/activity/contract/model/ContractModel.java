package com.app.tributum.activity.contract.model;

import java.util.List;

public class ContractModel {

    private String name;

    private String address;

    private String ppsNumber;

    private String email;

    private String occupation;

    private String maritalStatus;

    private String date;

    private String birthday;

    private List<String> employment;

    private List<String> taxes;

    private byte[] signature;

    private String message;

    private String startingDate;

    private String platform;

    public ContractModel(String name,
                         String address,
                         String ppsNumber,
                         String email,
                         String date,
                         String birthday,
                         String platform) {
        this.name = name;
        this.address = address;
        this.ppsNumber = ppsNumber;
        this.email = email;
        this.date = date;
        this.birthday = birthday;
        this.platform = platform;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPpsNumber(String ppsNumber) {
        this.ppsNumber = ppsNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmployment(List<String> employment) {
        this.employment = employment;
    }

    public void setTaxes(List<String> taxes) {
        this.taxes = taxes;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
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

    public String getPpsNumber() {
        return ppsNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getDate() {
        return date;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public List<String> getEmployment() {
        return employment;
    }

    public List<String> getTaxes() {
        return taxes;
    }

    public byte[] getSignature() {
        return signature;
    }
}