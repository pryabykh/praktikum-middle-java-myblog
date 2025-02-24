package com.pryabykh.service;

import com.pryabykh.dto.CreatePostDto;
import com.pryabykh.mapper.BlogMapper;
import com.pryabykh.model.Post;
import com.pryabykh.model.PostTag;
import com.pryabykh.model.Tag;
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
    private final PostTagRepository postTagRepository;

    public BlogServiceImpl(BlogMapper blogMapper,
                           PostRepository postRepository,
                           TagRepository tagRepository,
                           PostTagRepository postTagRepository) {
        this.blogMapper = blogMapper;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    @Override
    public long create(CreatePostDto dto) {
        Post post = blogMapper.mapToPost(dto);
        long postId = postRepository.save(post);
        post.getTags().stream().map(this::saveTag).forEach(tagId -> {
            postTagRepository.save(new PostTag(postId, tagId));
        });
        return postId;
    }

    private long saveTag(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DuplicateKeyException duplicateKeyException) {
            return tagRepository.findByContent(tag.getContent()).map(Tag::getId).orElseThrow();
        }
    }
}
