package com.pryabykh.dto;

import java.util.ArrayList;
import java.util.List;

public class CreatePostDto {

    private String title;

    private String base64Image;

    private String content;

    private List<String> tags = new ArrayList<>();

    public CreatePostDto() {
    }

    public CreatePostDto(String title, String base64Image, String content, List<String> tags) {
        this.title = title;
        this.base64Image = base64Image;
        this.content = content;
        this.tags = tags;
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
}
