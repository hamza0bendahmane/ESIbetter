package com.example.esibetter.articles;

public class Article_item {
    private String uid;
    private String title;
    private String body;

    private Long likes;
    private String PostId;
    private Long dislikes;
    private String image;
    private String Date;


    public Article_item() {
    }

    public Article_item(String uid, String title, String body, String image, String date, Long likes, Long disikes, String PostId) {
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.image = image;
        this.Date=date;
        this.likes = likes;
        this.dislikes = disikes;
        this.PostId = PostId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

}