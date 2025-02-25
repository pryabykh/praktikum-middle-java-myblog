package com.pryabykh.dto;

import java.util.List;

public class Page {

    private final int totalPages;

    private final int totalElements;

    private final List<PostDto> content;

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public List<PostDto> getContent() {
        return content;
    }

    public Page(int totalElements, int pageSize, List<PostDto> content) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Размер страницы должен быть больше 0");
        }
        if (totalElements <= 0) {
            this.totalPages = 0;
        } else {
            int tp = totalElements / pageSize;
            if (totalElements % pageSize != 0) {
                tp++;
            }
            this.totalPages = tp;
        }
        this.totalElements = totalElements;
        this.content = content;
    }
}
