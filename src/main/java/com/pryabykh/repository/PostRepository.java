package com.pryabykh.repository;

import com.pryabykh.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    long save(Post post);

    Optional<Post> findById(Long postId);

    int countByTag(String tag);

    List<Post> findAllByTag(String tag, int pageNumber, int pageSize);

    void deleteById(Long id);

    void incrementLikes(Long postId);
}
