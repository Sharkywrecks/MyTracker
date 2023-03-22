package com.TrackManInc.mytracker.Model;

public class Nutrients {
    private String carbs;
    private String protein;
    private String fat;
    private String salt;
    private String fibre;
    private String amount;
    private String serving;
    public Nutrients() {
    }

    public Nutrients(String carbs, String protein, String fat, String salt, String fibre,String amount,String serving) {
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.salt = salt;
        this.fibre = fibre;
        this.amount = amount;
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

    public String getFibre() {
        return fibre;
    }

    public void setFibre(String fibre) {
        this.fibre = fibre;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getServingSize() {
        return serving;
    }

    public void setServingSize(String serving) {
        this.serving = serving;
    }
}
