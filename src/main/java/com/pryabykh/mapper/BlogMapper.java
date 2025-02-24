package com.pryabykh.mapper;

import com.pryabykh.dto.CreatePostDto;
import com.pryabykh.model.Post;

public interface BlogMapper {

    Post mapToPost(CreatePostDto dto);
}
