package com.pryabykh.myblog.service;

import com.pryabykh.myblog.dto.CommentDto;
import com.pryabykh.myblog.dto.Page;
import com.pryabykh.myblog.dto.PostDto;
import com.pryabykh.myblog.mapper.BlogMapperImpl;
import com.pryabykh.myblog.model.Comment;
import com.pryabykh.myblog.model.Post;
import com.pryabykh.myblog.model.Tag;
import com.pryabykh.myblog.repository.CommentRepository;
import com.pryabykh.myblog.repository.PostRepository;
import com.pryabykh.myblog.repository.PostTagRepository;
import com.pryabykh.myblog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BlogServiceImpl.class, BlogMapperImpl.class})
public class BlogServiceImplTest {

    @Autowired
    private BlogService blogService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private TagRepository tagRepository;

    @MockitoBean
    private PostTagRepository postTagRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        Mockito.reset(postRepository, tagRepository, postTagRepository, commentRepository);
    }

    @Test
    void create_WhenValidDtoProvided_ShouldCreatePostPost() {
        when(postRepository.save(any(Post.class))).thenReturn(10L);
        when(tagRepository.save(eq(new Tag("tag1")))).thenReturn(1L);
        when(tagRepository.save(eq(new Tag("tag2")))).thenReturn(2L);
        when(tagRepository.save(eq(new Tag("tag3")))).thenThrow(DuplicateKeyException.class);
        when(tagRepository.findByContent("tag3")).thenReturn(Optional.of(new Tag(3L, "tag3")));

        long postId = blogService.createPost(
                new PostDto("title", "image", "content", List.of("tag1", "tag2", "tag3"))
        );

        assertEquals(10L, postId);
        verify(postRepository, times(1)).save(any());
        verify(tagRepository, times(3)).save(any());
        verify(tagRepository, times(1)).findByContent("tag3");
        verify(postTagRepository, times(3)).save(any());
    }

    @Test
    void update_WhenValidDtoProvided_ShouldUpdatePostPost() {
        when(postRepository.save(any(Post.class))).thenReturn(10L);
        when(tagRepository.save(eq(new Tag("tag1")))).thenReturn(1L);
        when(tagRepository.save(eq(new Tag("tag2")))).thenReturn(2L);
        when(tagRepository.save(eq(new Tag("tag3")))).thenThrow(DuplicateKeyException.class);
        when(tagRepository.findByContent("tag3")).thenReturn(Optional.of(new Tag(3L, "tag3")));

        long postId = blogService.updatePost(
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
    void delete_WhenPostExists_ShoudDeletePostPostWithAssotiations() {
        blogService.deletePost(1L);
        verify(postTagRepository, times(1)).deleteByPostId(eq(1L));
        verify(commentRepository, times(1)).deleteByPostId(eq(1L));
        verify(postRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void findPostById_WhenPostExists_ShouldReturnPost() {
        when(postRepository.findById(10L))
                .thenReturn(Optional.of(new Post(10L, "title", "image", "content", 100L, 5L)));
        when(tagRepository.findAllByPostId(10L)).thenReturn(List.of(
                new Tag(1L, "tag1"),
                new Tag(2L, "tag2")
        ));
        when(commentRepository.findAllByPostId(10L)).thenReturn(List.of(
                new Comment(1L, "comment1", 10L),
                new Comment(2L, "comment2", 10L)
        ));

        PostDto postDto = blogService.findPostById(10L);

        assertEquals(10L, postDto.getId());
        assertEquals("title", postDto.getTitle());
        assertEquals("image", postDto.getBase64Image());
        assertEquals("content", postDto.getContent());
        assertEquals(100L, postDto.getLikes());
        assertEquals(5L, postDto.getCommentsCount());

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

    @Test
    void findAll_Posts_WhenPostsExists_ShouldReturnPosts() {
        when(postRepository.findAllByTag(eq("tag1"), eq(0), eq(10))).thenReturn(List.of(
                new Post(1L, "title1", "image1", "content1", 1L, 1L),
                new Post(2L, "title2", "image2", "content2", 2L, 2L),
                new Post(3L, "title3", "image3", "content3", 3L, 3L),
                new Post(4L, "title4", "image4", "content4", 4L, 4L),
                new Post(5L, "title5", "image5", "content5", 5L, 5L)
        ));
        when(tagRepository.findAllByPostIdIn(eq(List.of(1L, 2L, 3L, 4L, 5L)))).thenReturn(List.of(
                new Tag(1L, "tag1", 1L),
                new Tag(2L, "tag2", 1L),
                new Tag(1L, "tag1", 2L),
                new Tag(1L, "tag1", 3L),
                new Tag(1L, "tag1", 4L),
                new Tag(1L, "tag1", 5L),
                new Tag(3L, "tag3", 5L)
        ));
        when(postRepository.countByTag(eq("tag1"))).thenReturn(5);

        Page page = blogService.findAllPosts("tag1", 0, 10);

        assertNotNull(page);
        assertEquals(1, page.getTotalPages());
        assertEquals(5, page.getTotalElements());
        assertNotNull(page.getContent());
        assertEquals(5, page.getContent().size());

        List<PostDto> posts = page.getContent();

        assertEquals(1L, posts.get(0).getId());
        assertEquals("title1", posts.get(0).getTitle());
        assertEquals("image1", posts.get(0).getBase64Image());
        assertEquals("content1", posts.get(0).getContent());
        assertEquals(1L, posts.get(0).getLikes());
        assertEquals(1L, posts.get(0).getCommentsCount());
        assertEquals(2, posts.get(0).getTags().size());
        assertEquals(List.of("tag1", "tag2"), posts.get(0).getTags());

        assertEquals(2L, posts.get(1).getId());
        assertEquals("title2", posts.get(1).getTitle());
        assertEquals("image2", posts.get(1).getBase64Image());
        assertEquals("content2", posts.get(1).getContent());
        assertEquals(2L, posts.get(1).getLikes());
        assertEquals(2L, posts.get(1).getCommentsCount());
        assertEquals(1, posts.get(1).getTags().size());
        assertEquals(List.of("tag1"), posts.get(1).getTags());

        assertEquals(3L, posts.get(2).getId());
        assertEquals("title3", posts.get(2).getTitle());
        assertEquals("image3", posts.get(2).getBase64Image());
        assertEquals("content3", posts.get(2).getContent());
        assertEquals(3L, posts.get(2).getLikes());
        assertEquals(3L, posts.get(2).getCommentsCount());
        assertEquals(1, posts.get(2).getTags().size());
        assertEquals(List.of("tag1"), posts.get(2).getTags());

        assertEquals(4L, posts.get(3).getId());
        assertEquals("title4", posts.get(3).getTitle());
        assertEquals("image4", posts.get(3).getBase64Image());
        assertEquals("content4", posts.get(3).getContent());
        assertEquals(4L, posts.get(3).getLikes());
        assertEquals(4L, posts.get(3).getCommentsCount());
        assertEquals(1, posts.get(3).getTags().size());
        assertEquals(List.of("tag1"), posts.get(3).getTags());

        assertEquals(5L, posts.get(4).getId());
        assertEquals("title5", posts.get(4).getTitle());
        assertEquals("image5", posts.get(4).getBase64Image());
        assertEquals("content5", posts.get(4).getContent());
        assertEquals(5L, posts.get(4).getLikes());
        assertEquals(5L, posts.get(4).getCommentsCount());
        assertEquals(2, posts.get(4).getTags().size());
        assertEquals(List.of("tag1", "tag3"), posts.get(4).getTags());

        verify(postRepository, times(1)).findAllByTag(eq("tag1"), eq(0), eq(10));
        verify(tagRepository, times(1)).findAllByPostIdIn(eq(List.of(1L, 2L, 3L, 4L, 5L)));
        verify(postRepository, times(1)).countByTag(eq("tag1"));
    }

    @Test
    void likePost_whenCalled_ShouldAddLike() {
        blogService.likePost(10L);
        verify(postRepository, times(1)).incrementLikes(eq(10L));
    }

    @Test
    void addComment_WhenValidCommentProvided_ShouldCreateComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(10L);

        long commentId = blogService.addComment(10L, new CommentDto("comment"));

        assertEquals(10L, commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_WhenValidCommentProvided_ShouldUpdateComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(10L);

        long commentId = blogService.updateComment(10L, new CommentDto("comment"));

        assertEquals(10L, commentId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void deleteComment_WhenCommentExists_ShouldBeDeleted() {
        blogService.deleteComment(10L);
        verify(commentRepository, times(1)).deleteById(eq(10L));
    }
}
