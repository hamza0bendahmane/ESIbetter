package com.example.esibetter.notifications;

public class Notifi_item {


    Long dislikes;
    Long likes;
    Long time;
    boolean seen;
    String post_id;
    String title;
    String date;
    String body;
    String image;
    String type;
    String name;
    String reply;
    String number;
    String comment;

    public Notifi_item() {
    }

    public Notifi_item(Long dislikes, Long likes, Long time, boolean seen,
                       String post_id, String title, String date, String body, String image,
                       String type, String name, String number, String comment, String reply) {
        this.dislikes = dislikes;
        this.likes = likes;
        this.time = time;
        this.seen = seen;
        this.post_id = post_id;
        this.title = title;
        this.date = date;
        this.body = body;
        this.image = image;
        this.type = type;
        this.name = name;
        this.number = number;
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
