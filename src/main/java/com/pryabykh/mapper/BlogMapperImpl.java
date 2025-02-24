package com.pryabykh.mapper;

import com.pryabykh.dto.CommentDto;
import com.pryabykh.dto.PostDto;
import com.pryabykh.model.Comment;
import com.pryabykh.model.Post;
import com.pryabykh.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class BlogMapperImpl implements BlogMapper {

    public Post mapToPost(PostDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setBase64Image(dto.getBase64Image());
        post.setContent(dto.getContent());
        dto.getTags().forEach(tag -> {
            post.getTags().add(new Tag(tag));
        });
        return post;
    }

    @Override
    public PostDto toPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setLikes(post.getLikes());
        postDto.setBase64Image(post.getBase64Image());
        post.setLikes(post.getLikes());
        postDto.setCommentsCount(post.getCommentsCount());
        return postDto;
    }

    @Override
    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        return commentDto;
    }
}
