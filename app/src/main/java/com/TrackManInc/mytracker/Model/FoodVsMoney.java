package com.TrackManInc.mytracker.Model;

import java.util.ArrayList;

public class FoodVsMoney {
    private String date,money;
    private ArrayList<String> foodNames;

    public FoodVsMoney(String date, String money, ArrayList<String> foodNames) {
        this.date = date;
        this.money = money;
        this.foodNames = foodNames;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public ArrayList<String> getFoodNames() {
        return foodNames;
    }

    public void setFoodNames(ArrayList<String> foodNames) {
        this.foodNames = foodNames;
    }
}
