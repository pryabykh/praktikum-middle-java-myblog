package com.pryabykh.model;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private String title;

    private String base64Image;

    private String content;

    private Long likes;

    private List<Tag> tags = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();
}
