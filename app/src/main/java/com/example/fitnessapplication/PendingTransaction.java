package com.example.fitnessapplication;

public class PendingTransaction {
    private String subscriptionName;
    private String subscriptionDescription;
    private String paymentID;
    private String price;
    private String status;
    private String paymentMethod;
    private String subscriptionDate;

    // Constructor, getters, and setters
    public PendingTransaction(String subscriptionName, String subscriptionDescription, String paymentID, String price, String status, String paymentMethod, String subscriptionDate) {
        this.subscriptionName = subscriptionName;
        this.subscriptionDescription = subscriptionDescription;
        this.paymentID = paymentID;
        this.price = price;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.subscriptionDate = subscriptionDate;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionDescription() {
        return subscriptionDescription;
    }

    public void setSubscriptionDescription(String subscriptionDescription) {
        this.subscriptionDescription = subscriptionDescription;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(String subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
