package com.TrackManInc.mytracker.Model;

public class Users {
    private String name,password,email,image,lifetime_amount,streak,previous_date_streak;
    public Users(){

    }

    public Users(String name, String password, String email, String image, String lifetime_amount,String streak,String previous_date_streak) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.image = image; // url
        this.lifetime_amount = lifetime_amount;
        this.streak=streak;
        this.previous_date_streak = previous_date_streak;
    }

    public String getLifetime_amount() {
        return lifetime_amount;
    }

    public void setLifetime_amount(String lifetime_amount) {
        this.lifetime_amount = lifetime_amount;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStreak() {
        return streak;
    }

    public void setStreak(String streak) {
        this.streak = streak;
    }

    public String getPrevious_date_streak() {
        return previous_date_streak;
    }

    public void setPrevious_date_streak(String previous_date_streak) {
        this.previous_date_streak = previous_date_streak;
    }
}
