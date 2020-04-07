package com.example.esibetter.articles;

class comment_item {

    String commentBody ;
    int Likes ;
    String CommenterUid;

    public comment_item(String commentBody, int likes, String commenterUid) {
        this.commentBody = commentBody;
        Likes = likes;
        CommenterUid = commenterUid;
    }


    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public int getLikes() {
        return Likes;
    }

    public void setLikes(int likes) {
        Likes = likes;
    }

    public String getCommenterUid() {
        return CommenterUid;
    }

    public void setCommenterUid(String commenterUid) {
        CommenterUid = commenterUid;
    }
}
