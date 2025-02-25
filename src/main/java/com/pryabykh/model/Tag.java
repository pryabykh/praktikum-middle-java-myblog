package com.pryabykh.model;

import java.util.Objects;

public class Tag {

    private Long id;

    private String content;

    private Long postId;

    public Tag() {
    }

    public Tag(String content) {
        this.content = content;
    }

    public Tag(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Tag(Long id, String content, Long postId) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;
        return Objects.equals(content, tag.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }
}
