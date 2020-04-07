package com.example.esibetter.articles;

import android.net.Uri;

import java.util.ArrayList;

public class Article_item {
    private String uid;
    private String title;
    private String body;
    private int likes;
    private int dislikes;
    private ArrayList<comment_item> Comments;
    private Uri image;
    private String Date;

    public Article_item(String uid, String title, String body, ArrayList<comment_item> comments, Uri image, String date, int likes, int disikes) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.Comments=comments;
        this.image = image;
        this.Date = date;
        this.likes = likes;
        this.dislikes = disikes;
    }

    public Article_item() {
    }

    public Article_item(String uid, String title, String body, Uri image, String date, int likes, int disikes) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.image = image;
        this.Date=date;
        this.likes = likes;
        this.dislikes = disikes;
    }

    public ArrayList<comment_item> getComments() {
        return Comments;
    }

    public void setComments(ArrayList<comment_item> comments) {
        Comments = comments;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String posterName) {
        uid = posterName;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
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