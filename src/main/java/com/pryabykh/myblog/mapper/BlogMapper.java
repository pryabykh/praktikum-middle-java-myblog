package com.pryabykh.myblog.mapper;

import com.pryabykh.myblog.dto.CommentDto;
import com.pryabykh.myblog.dto.PostDto;
import com.pryabykh.myblog.model.Comment;
import com.pryabykh.myblog.model.Post;

public interface BlogMapper {

    Post mapToPost(PostDto dto);

    PostDto toPostDto(Post post);

    Comment mapToComment(CommentDto dto);

    CommentDto toCommentDto(Comment comment);
}
