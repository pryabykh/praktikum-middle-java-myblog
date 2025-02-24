package com.pryabykh.service;

import com.pryabykh.dto.CreatePostDto;

public interface BlogService {

    long create(CreatePostDto dto);
}
