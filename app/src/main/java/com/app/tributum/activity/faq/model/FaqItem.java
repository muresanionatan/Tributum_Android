package com.app.tributum.activity.faq.model;

public class FaqItem {

    @FaqItemState
    private int state;

    private String title;

    private String subtitle;

    public FaqItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}