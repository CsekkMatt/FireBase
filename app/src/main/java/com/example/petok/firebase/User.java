package com.example.petok.firebase;

/**
 * Created by petok on 2018. 01. 02..
 */

public class User {
    private String image,name,phone;

    public User() {

    }


    public User(String image, String name, String phone) {
        this.image = image;
        this.name = name;
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
