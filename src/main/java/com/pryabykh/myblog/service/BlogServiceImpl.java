package com.pryabykh.myblog.service;

import com.pryabykh.myblog.dto.CommentDto;
import com.pryabykh.myblog.dto.Page;
import com.pryabykh.myblog.dto.PostDto;
import com.pryabykh.myblog.mapper.BlogMapper;
import com.pryabykh.myblog.model.Comment;
import com.pryabykh.myblog.model.Post;
import com.pryabykh.myblog.model.PostTag;
import com.pryabykh.myblog.model.Tag;
import com.pryabykh.myblog.repository.CommentRepository;
import com.pryabykh.myblog.repository.PostRepository;
import com.pryabykh.myblog.repository.PostTagRepository;
import com.pryabykh.myblog.repository.TagRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public long createPost(PostDto dto) {
        Post post = blogMapper.mapToPost(dto);
        long postId = postRepository.save(post);
        post.getTags().stream().map(this::saveTag).forEach(tagId -> {
            postTagRepository.save(new PostTag(postId, tagId));
        });
        return postId;
    }

    @Override
    public long updatePost(Long postId, PostDto dto) {
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
    public void deletePost(Long postId) {
        commentRepository.deleteByPostId(postId);
        postTagRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    @Override
    public PostDto findPostById(Long postId) {
        return postRepository.findById(postId)
                .map(blogMapper::toPostDto)
                .map(postDto -> {
                    tagRepository.findAllByPostId(postDto.getId())
                            .forEach(tag -> postDto.getTags().add(tag.getContent()));
                    commentRepository.findAllByPostId(postDto.getId())
                            .forEach(comment -> postDto.getComments().add(blogMapper.toCommentDto(comment)));
                    postDto.setContent(postDto.getContent().replace("\r\n", "<br>").replace("\n", "<br>"));
                    return postDto;
                })
                .orElseThrow();
    }

    @Override
    public Page findAllPosts(String tag, int pageNumber, int pageSize) {
        List<PostDto> posts = postRepository.findAllByTag(tag, pageNumber, pageSize)
                .stream()
                .map(blogMapper::toPostDto)
                .peek(postDto -> postDto.setContent(cutContent(postDto.getContent())))
                .toList();
        Map<Long, List<Tag>> tagsByPostId = tagRepository.findAllByPostIdIn(posts.stream().map(PostDto::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(Tag::getPostId));
        posts.forEach(post -> {
            if (tagsByPostId.get(post.getId()) != null) {
                post.setTags(tagsByPostId.get(post.getId()).stream().map(Tag::getContent).toList());
            }
        });
        return new Page(
                postRepository.countByTag(tag),
                pageSize,
                posts
        );
    }

    @Override
    public void likePost(long postId) {
        postRepository.incrementLikes(postId);
    }

    @Override
    public long addComment(long postId, CommentDto dto) {
        Comment comment = blogMapper.mapToComment(dto);
        comment.setPostId(postId);
        return commentRepository.save(comment);
    }

    @Override
    public long updateComment(long commentId, CommentDto dto) {
        Comment comment = blogMapper.mapToComment(dto);
        comment.setId(commentId);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private long saveTag(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DuplicateKeyException duplicateKeyException) {
            return tagRepository.findByContent(tag.getContent()).map(Tag::getId).orElseThrow();
        }
    }

    private String cutContent(String content) {
        int charactersSize = 230;
        if (content.length() > charactersSize) {
            return content.substring(0, charactersSize) + "...";
        } else {
            return content;
        }
    }
}
