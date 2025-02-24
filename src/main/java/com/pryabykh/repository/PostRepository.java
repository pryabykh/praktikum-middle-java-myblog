package com.pryabykh.repository;

import com.pryabykh.model.Post;

import java.util.Optional;

public interface PostRepository {


    long save(Post post);

    Optional<Post> findById(Long postId);

    long countByTag(String tag);
}
