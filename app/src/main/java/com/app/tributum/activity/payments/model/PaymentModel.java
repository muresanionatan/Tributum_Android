package com.app.tributum.activity.payments.model;

/**
 * Model class that contains the payment information
 */
public class PaymentModel {

    /**
     * The name of the person to which the payment will be made
     */
    private String name;

    /**
     * The pps of the person to which the payment will be made
     */
    private String pps;

    /**
     * The amount that will be paid for the given name
     */
    private String amount;

    /**
     * The site location/ID for the given name
     */
    private String site;

    private boolean nameFocus;

    private boolean ppsFocus;

    private boolean amountFocus;

    private boolean siteFocus;

    public PaymentModel(String name, String pps, String amount, String site) {
        this.name = name;
        this.pps = pps;
        this.amount = amount;
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPps() {
        return pps;
    }

    public void setPps(String pps) {
        this.pps = pps;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public boolean isNameFocus() {
        return nameFocus;
    }

    public void setNameFocus(boolean nameFocus) {
        this.nameFocus = nameFocus;
    }

    public boolean isPpsFocus() {
        return ppsFocus;
    }

    public void setPpsFocus(boolean ppsFocus) {
        this.ppsFocus = ppsFocus;
    }

    public boolean isAmountFocus() {
        return amountFocus;
    }

    public void setAmountFocus(boolean amountFocus) {
        this.amountFocus = amountFocus;
    }

    public boolean isSiteFocus() {
        return siteFocus;
    }

    public void setSiteFocus(boolean siteFocus) {
        this.siteFocus = siteFocus;
    }
}