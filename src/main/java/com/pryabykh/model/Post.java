package com.pryabykh.model;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private String title;

    private String base64Image;

    private String content;

    private Long likes;

    private Long commentsCount;

    private List<Tag> tags = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String base64Image, String content) {
        this.title = title;
        this.base64Image = base64Image;
        this.content = content;
    }

    public Post(Long id, String title, String base64Image, String content) {
        this.id = id;
        this.title = title;
        this.base64Image = base64Image;
        this.content = content;
    }

    public Post(Long id, String title, String base64Image, String content, Long likes, Long commentsCount) {
        this.id = id;
        this.title = title;
        this.base64Image = base64Image;
        this.content = content;
        this.likes = likes;
        this.commentsCount = commentsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
