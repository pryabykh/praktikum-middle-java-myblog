package com.pryabykh.model;

import java.util.Objects;

public class Tag {

    private Long id;

    private String content;

    public Tag() {
    }

    public Tag(String content) {
        this.content = content;
    }

    public Tag(Long id, String content) {
        this.id = id;
        this.content = content;
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
