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

    public PaymentModel(String name, String pps, String amount) {
        this.name = name;
        this.pps = pps;
        this.amount = amount;
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
}