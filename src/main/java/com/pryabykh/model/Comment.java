package com.pryabykh.model;

public class Comment {

    private Long id;

    private String content;

    private Long postId;

    public Comment() {
    }

    public Comment(String content, Long postId) {
        this.content = content;
        this.postId = postId;
    }

    public Comment(String content) {
        this.content = content;
    }

    public Comment(Long id, String content, Long postId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
