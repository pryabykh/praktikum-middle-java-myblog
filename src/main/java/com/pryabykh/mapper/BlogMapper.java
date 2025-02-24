package com.pryabykh.mapper;

import com.pryabykh.dto.PostDto;
import com.pryabykh.model.Post;

public interface BlogMapper {

    Post mapToPost(PostDto dto);
}
