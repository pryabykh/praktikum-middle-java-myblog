package com.pryabykh.service;

import com.pryabykh.dto.CommentDto;
import com.pryabykh.dto.Page;
import com.pryabykh.dto.PostDto;

public interface BlogService {

    long createPost(PostDto dto);

    long updatePost(Long postId, PostDto dto);

    void deletePost(Long postId);

    PostDto findPostById(Long postId);

    Page findAllPosts(String tag, int pageNumber, int pageSize);

    long addComment(long postId, CommentDto dto);
}
