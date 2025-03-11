package com.pryabykh.myblog.repository;


import com.pryabykh.myblog.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    long save(Tag tag);

    List<Tag> findAllByPostId(Long postId);

    List<Tag> findAllByPostIdIn(List<Long> postIds);

    Optional<Tag> findById(Long tagId);

    Optional<Tag> findByContent(String content);
}
