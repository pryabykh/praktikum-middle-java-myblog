package com.pryabykh.repository;

import com.pryabykh.model.PostTag;

public interface PostTagRepository {

    void save(PostTag postTag);

    void deleteByPostId(Long postId);
}
