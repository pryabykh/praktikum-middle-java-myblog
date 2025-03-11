package com.pryabykh.myblog.repository;

import com.pryabykh.myblog.model.Comment;

import java.util.List;

public interface CommentRepository {

    long save(Comment tag);

    List<Comment> findAllByPostId(long postId);

    void deleteById(Long id);

    void deleteByPostId(Long postId);
}
