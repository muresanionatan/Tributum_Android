package com.app.tributum.model;

public class EmailBody {

    private String email;

    private String message;

    private String platform;

    public EmailBody(String email, String message, String platform) {
        this.email = email;
        this.message = message;
        this.platform = platform;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}