package com.example.esibetter.articles;

class comment_item {

    String comment;
    Long likes;
    String date;
    String uid;
    String commentId;

    public comment_item(String comment, Long likes, String uid, String commentId) {
        this.comment = comment;
        this.likes = likes;
        this.uid = uid;
        this.commentId = commentId;

    }

    public comment_item() {

    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
