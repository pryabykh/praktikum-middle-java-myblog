package com.pryabykh.service;

import com.pryabykh.dto.PostDto;
import com.pryabykh.mapper.BlogMapperImpl;
import com.pryabykh.model.Comment;
import com.pryabykh.model.Post;
import com.pryabykh.model.Tag;
import com.pryabykh.repository.CommentRepository;
import com.pryabykh.repository.PostRepository;
import com.pryabykh.repository.PostTagRepository;
import com.pryabykh.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {BlogServiceImpl.class, BlogMapperImpl.class, BlogServiceImplTest.DaoConfig.class})
public class BlogServiceImplTest {

    @Autowired
    private BlogService blogService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository, tagRepository, postTagRepository);
    }

    @Test
    void create_WhenValidDtoProvided_ShouldCreatePost() {
        when(postRepository.save(any(Post.class))).thenReturn(10L);
        when(tagRepository.save(eq(new Tag("tag1")))).thenReturn(1L);
        when(tagRepository.save(eq(new Tag("tag2")))).thenReturn(2L);
        when(tagRepository.save(eq(new Tag("tag3")))).thenThrow(DuplicateKeyException.class);
        when(tagRepository.findByContent("tag3")).thenReturn(Optional.of(new Tag(3L, "tag3")));

        long postId = blogService.create(
                new PostDto("title", "image", "content", List.of("tag1", "tag2", "tag3"))
        );

        assertEquals(10L, postId);
        verify(postRepository, times(1)).save(any());
        verify(tagRepository, times(3)).save(any());
        verify(tagRepository, times(1)).findByContent("tag3");
        verify(postTagRepository, times(3)).save(any());
    }

    @Test
    void update_WhenValidDtoProvided_ShouldUpdatePost() {
        when(postRepository.save(any(Post.class))).thenReturn(10L);
        when(tagRepository.save(eq(new Tag("tag1")))).thenReturn(1L);
        when(tagRepository.save(eq(new Tag("tag2")))).thenReturn(2L);
        when(tagRepository.save(eq(new Tag("tag3")))).thenThrow(DuplicateKeyException.class);
        when(tagRepository.findByContent("tag3")).thenReturn(Optional.of(new Tag(3L, "tag3")));

        long postId = blogService.update(
                10L,
                new PostDto("title", "image", "content", List.of("tag1", "tag2", "tag3"))
        );

        assertEquals(10L, postId);
        verify(postRepository, times(1)).save(any());
        verify(tagRepository, times(3)).save(any());
        verify(tagRepository, times(1)).findByContent("tag3");
        verify(postTagRepository, times(1)).deleteByPostId(eq(10L));
        verify(postTagRepository, times(3)).save(any());
    }

    @Test
    void findById_WhenPostExists_ShouldReturnPost() {
        when(postRepository.findById(10L))
                .thenReturn(Optional.of(new Post(10L, "title", "image", "content")));
        when(tagRepository.findAllByPostId(10L)).thenReturn(List.of(
                new Tag(1L, "tag1"),
                new Tag(2L, "tag2")
        ));
        when(commentRepository.findAllByPostId(10L)).thenReturn(List.of(
                new Comment(1L, "comment1", 10L),
                new Comment(2L, "comment2", 10L)
        ));

        PostDto postDto = blogService.findById(10L);

        assertEquals(10L, postDto.getId());
        assertEquals("title", postDto.getTitle());
        assertEquals("image", postDto.getBase64Image());
        assertEquals("content", postDto.getContent());

        assertEquals("tag1", postDto.getTags().get(0));
        assertEquals("tag2", postDto.getTags().get(1));

        assertEquals(1L, postDto.getComments().get(0).getId());
        assertEquals("comment1", postDto.getComments().get(0).getContent());

        assertEquals(2L, postDto.getComments().get(1).getId());
        assertEquals("comment2", postDto.getComments().get(1).getContent());

        verify(postRepository, times(1)).findById(eq(10L));
        verify(tagRepository, times(1)).findAllByPostId(eq(10L));
        verify(commentRepository, times(1)).findAllByPostId(eq(10L));
    }

    @Configuration
    static class DaoConfig {

        @Bean
        public PostRepository postRepository() {
            return Mockito.mock(PostRepository.class);
        }

        @Bean
        public TagRepository tagRepository() {
            return Mockito.mock(TagRepository.class);
        }

        @Bean
        public PostTagRepository postTagRepository() {
            return Mockito.mock(PostTagRepository.class);
        }

        @Bean
        public CommentRepository commentRepository() {
            return Mockito.mock(CommentRepository.class);
        }
    }
}
