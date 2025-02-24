package com.pryabykh.service;

import com.pryabykh.dto.Page;
import com.pryabykh.dto.PostDto;

public interface BlogService {

    long create(PostDto dto);

    long update(Long postId, PostDto dto);

    PostDto findById(Long postId);

    Page findAll(String tag, int pageNumber, int pageSize);
}
