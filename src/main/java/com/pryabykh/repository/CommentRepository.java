package com.pryabykh.repository;

import com.pryabykh.model.Comment;

import java.util.List;

public interface CommentRepository {

    long save(Comment tag);

    List<Comment> findAllByPostId(long postId);
}
