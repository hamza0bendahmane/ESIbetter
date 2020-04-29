package com.example.esibetter.articles;

import android.net.Uri;

public class Article_item {
    private String uid;
    private String title;
    private String body;
    private Long likes;
    private Long dislikes;
    private Uri image;
    private String Date;


    public Article_item() {
    }

    public Article_item(String uid, String title, String body, Uri image, String date, Long likes, Long disikes) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.image = image;
        this.Date=date;
        this.likes = likes;
        this.dislikes = disikes;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String posterName) {
        uid = posterName;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

}