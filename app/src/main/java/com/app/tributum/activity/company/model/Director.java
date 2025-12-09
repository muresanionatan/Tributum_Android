package com.app.tributum.activity.company.model;

public class Director {

    private String firstName;
    private String surName;
    private String birthday;
    private String pps;
    private String nationality;
    private String occupation;
    private String address;
    private String directorships;
    private String other;

    private String ppsFrontFile;
    private String ppsBackFile;
    private String idFile;
    private String passport;

    public Director() {
    }

    public Director(String firstName, String surName, String birthday, String pps, String nationality, String occupation, String address, String directorships, String other) {
        this.firstName = firstName;
        this.surName = surName;
        this.birthday = birthday;
        this.pps = pps;
        this.nationality = nationality;
        this.occupation = occupation;
        this.address = address;
        this.directorships = directorships;
        this.other = other;
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDirectorships() {
        return directorships;
    }

    public void setDirectorships(String directorships) {
        this.directorships = directorships;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getPpsFrontFile() {
        return ppsFrontFile;
    }

    public void setPpsFrontFile(String ppsFrontFile) {
        this.ppsFrontFile = ppsFrontFile;
    }

    public String getPpsBackFile() {
        return ppsBackFile;
    }

    public void setPpsBackFile(String ppsBackFile) {
        this.ppsBackFile = ppsBackFile;
    }

    public String getIdFile() {
        return idFile;
    }

    public void setIdFile(String idFile) {
        this.idFile = idFile;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
