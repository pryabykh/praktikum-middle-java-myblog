package com.pryabykh.service;

import com.pryabykh.dto.PostDto;

public interface BlogService {

    long create(PostDto dto);

    long update(Long postId, PostDto dto);
}
