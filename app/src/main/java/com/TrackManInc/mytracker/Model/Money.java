package com.TrackManInc.mytracker.Model;

public class Money {

    private double[] year;
    private double[] month;
    private double[] week;
    public Money() {

    }

    public Money(double[] year, double[] month, double[] week) {
        this.year = year;
        this.month = month;
        this.week = week;
    }

    public double[] getYear() {
        return year;
    }

    public void setYear(double[] year) {
        this.year = year;
    }

    public double[] getMonth() {
        return month;
    }

    public void setMonth(double[] month) {
        this.month = month;
    }

    public double[] getWeek() {
        return week;
    }

    public void setWeek(double[] week) {
        this.week = week;
    }
}
