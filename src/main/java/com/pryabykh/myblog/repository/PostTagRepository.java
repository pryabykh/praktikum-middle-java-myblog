package com.pryabykh.myblog.repository;

import com.pryabykh.myblog.model.PostTag;

public interface PostTagRepository {

    void save(PostTag postTag);

    void deleteByPostId(Long postId);
}
