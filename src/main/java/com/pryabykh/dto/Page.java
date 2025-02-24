package com.pryabykh.dto;

import java.util.List;

public class Page {

    private int totalPages;

    private int totalElements;

    private List<PostDto> content;

    public Page(int totalElements, int pageSize, List<PostDto> content) {
        if (totalElements == 0) {
            this.totalPages = 0;
        } else {
            this.totalPages = totalElements / pageSize;
        }
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;
    }
}
