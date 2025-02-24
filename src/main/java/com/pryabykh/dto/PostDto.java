package com.pryabykh.dto;

import java.util.ArrayList;
import java.util.List;

public class PostDto {

    private Long id;

    private String title;

    private String base64Image;

    private String content;

    private List<String> tags = new ArrayList<>();

    private List<CommentDto> comments = new ArrayList<>();

    public PostDto() {
    }

    public PostDto(String title, String base64Image, String content, List<String> tags) {
        this.title = title;
        this.base64Image = base64Image;
        this.content = content;
        this.tags = tags;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}
