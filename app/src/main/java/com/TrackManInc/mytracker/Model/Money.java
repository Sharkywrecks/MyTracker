package com.TrackManInc.mytracker.Model;

public class Money {
    private String date, amount;

    public Money() {

    }

    public Money(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return this.date;
    }

    public String getAmount() {
        return this.amount;
    }

    void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
