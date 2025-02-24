package com.pryabykh.service;

import com.pryabykh.dto.PostDto;
import com.pryabykh.mapper.BlogMapper;
import com.pryabykh.model.Post;
import com.pryabykh.model.PostTag;
import com.pryabykh.model.Tag;
import com.pryabykh.repository.CommentRepository;
import com.pryabykh.repository.PostRepository;
import com.pryabykh.repository.PostTagRepository;
import com.pryabykh.repository.TagRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl implements BlogService {
    private final BlogMapper blogMapper;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final PostTagRepository postTagRepository;

    public BlogServiceImpl(BlogMapper blogMapper,
                           PostRepository postRepository,
                           TagRepository tagRepository,
                           CommentRepository commentRepository,
                           PostTagRepository postTagRepository) {
        this.blogMapper = blogMapper;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.postTagRepository = postTagRepository;
    }

    @Override
    public long create(PostDto dto) {
        Post post = blogMapper.mapToPost(dto);
        long postId = postRepository.save(post);
        post.getTags().stream().map(this::saveTag).forEach(tagId -> {
            postTagRepository.save(new PostTag(postId, tagId));
        });
        return postId;
    }

    @Override
    public long update(Long postId, PostDto dto) {
        Post post = blogMapper.mapToPost(dto);
        post.setId(postId);
        postRepository.save(post);
        postTagRepository.deleteByPostId(postId);
        post.getTags().stream().map(this::saveTag).forEach(tagId -> {
            postTagRepository.save(new PostTag(postId, tagId));
        });
        return postId;
    }

    @Override
    public PostDto findById(Long postId) {
        return postRepository.findById(postId)
                .map(blogMapper::toPostDto)
                .map(postDto -> {
                    tagRepository.findAllByPostId(postDto.getId())
                            .forEach(tag -> postDto.getTags().add(tag.getContent()));
                    commentRepository.findAllByPostId(postDto.getId())
                            .forEach(comment -> postDto.getComments().add(blogMapper.toCommentDto(comment)));
                    return postDto;
                })
                .orElseThrow();
    }

    private long saveTag(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DuplicateKeyException duplicateKeyException) {
            return tagRepository.findByContent(tag.getContent()).map(Tag::getId).orElseThrow();
        }
    }
}
