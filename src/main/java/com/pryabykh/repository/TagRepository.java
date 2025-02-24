package com.pryabykh.repository;

import com.pryabykh.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    long save(Tag tag);

    List<Tag> findAllByPostId(Long postId);

    Optional<Tag> findById(Long tagId);

    Optional<Tag> findByContent(String content);
}
