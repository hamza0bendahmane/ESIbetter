package com.example.esibetter.courses;


public class File_item {
    private String uid;
    private String title;
    private String PostId;
    private String module;
    private String thumbnail;
    private String date;

    public File_item() {
    }

    public File_item(String uid, String title, String postId, String module, String thumbnail , String date) {
        this.uid = uid;
        this.title = title;
        PostId = postId;
        this.module = module;
        this.thumbnail = thumbnail;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
