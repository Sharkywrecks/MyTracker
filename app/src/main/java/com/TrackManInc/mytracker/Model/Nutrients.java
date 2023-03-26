package com.TrackManInc.mytracker.Model;

public class Nutrients {
    private String carbs;
    private String protein;
    private String fat;
    private String salt;
    private String fiber;
    private String quantity;
    private String serving;
    public Nutrients() {
    }

    public Nutrients(String carbs,String fat, String fiber,String protein, String quantity, String salt, String serving) {
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.salt = salt;
        this.fiber = fiber;
        this.quantity = quantity;
        this.serving = serving;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFiber() {
        return fiber;
    }

    public void setFiber(String fiber) {
        this.fiber = fiber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getServing() {
        return serving;
    }

    public void setServing(String serving) {
        this.serving = serving;
    }
}
