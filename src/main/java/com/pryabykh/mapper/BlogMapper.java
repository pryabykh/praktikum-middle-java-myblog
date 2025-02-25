package com.pryabykh.mapper;

import com.pryabykh.dto.CommentDto;
import com.pryabykh.dto.PostDto;
import com.pryabykh.model.Comment;
import com.pryabykh.model.Post;

public interface BlogMapper {

    Post mapToPost(PostDto dto);

    PostDto toPostDto(Post post);

    Comment mapToComment(CommentDto dto);

    CommentDto toCommentDto(Comment comment);
}
