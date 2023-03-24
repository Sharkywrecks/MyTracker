package com.TrackManInc.mytracker.Model;

public class Users {
    private String name,password,email,image,lifetime_amount;

    public Users(){

    }

    public Users(String name, String password, String email, String image, String lifetime_amount) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.image = image; // url
        this.lifetime_amount = lifetime_amount;
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
}
