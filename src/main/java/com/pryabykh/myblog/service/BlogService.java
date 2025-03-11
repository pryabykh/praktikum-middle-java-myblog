package com.pryabykh.myblog.service;

import com.pryabykh.myblog.dto.CommentDto;
import com.pryabykh.myblog.dto.Page;
import com.pryabykh.myblog.dto.PostDto;

public interface BlogService {

    long createPost(PostDto dto);

    long updatePost(Long postId, PostDto dto);

    void deletePost(Long postId);

    PostDto findPostById(Long postId);

    Page findAllPosts(String tag, int pageNumber, int pageSize);

    void likePost(long postId);

    long addComment(long postId, CommentDto dto);

    long updateComment(long commentId, CommentDto dto);

    void deleteComment(Long commentId);
}
