package com.pryabykh.mapper;

import com.pryabykh.dto.CreatePostDto;
import com.pryabykh.model.Post;
import com.pryabykh.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class BlogMapperImpl implements BlogMapper {

    public Post mapToPost(CreatePostDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setBase64Image(dto.getBase64Image());
        post.setContent(dto.getContent());
        dto.getTags().forEach(tag -> {
            post.getTags().add(new Tag(tag));
        });
        return post;
    }
}
