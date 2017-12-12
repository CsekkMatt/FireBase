package com.example.petok.firebase;

/**
 * Created by petok on 2017. 12. 12..
 */

public class Blog {
    private String title,desc,image,email;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blog(String title, String desc, String image, String email) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.email = email;
    }
    public Blog(){


    }
}

