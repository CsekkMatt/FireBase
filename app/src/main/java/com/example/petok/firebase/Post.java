package com.example.petok.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by petok on 2017. 12. 15..
 */

public class Post {
    private String Desc, Title, image,uid,username;

    public Post() {

    }

    public Post(String desc, String title, String image,String uid,String username) {
        Desc = desc;
        Title = title;
        this.image = image;
        this.uid  = uid;
        this.username = username;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}



