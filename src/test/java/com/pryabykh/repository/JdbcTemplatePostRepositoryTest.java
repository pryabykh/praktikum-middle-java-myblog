package com.pryabykh.repository;

import com.pryabykh.model.Post;
import configuration.H2DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {H2DataSourceConfiguration.class, JdbcTemplatePostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcTemplatePostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
    }


    @Test
    void save_whenValidPostWithoutId_ShouldInsertPostToDatabase() {
        Post post = new Post("Название поста", "base64data", "text content");

        long postId = postRepository.save(post);

        Post savedPost = findPostById(postId);

        assertNotNull(savedPost);
        assertEquals(postId, savedPost.getId());
        assertEquals("Название поста", savedPost.getTitle());
        assertEquals("base64data", savedPost.getBase64Image());
        assertEquals("text content", savedPost.getContent());
        assertEquals(0, savedPost.getLikes());
    }

    @Test
    void save_whenValidPostWithId_ShouldUpdatePostToDatabase() {
        Post post = new Post("Название поста", "base64data", "text content");
        long postId = postRepository.save(post);
        Post updatedPost = new Post(postId, "Новое название поста", "newBase64data", "new text content");

        postRepository.save(updatedPost);

        Post savedUpdatedPost = findPostById(postId);

        assertNotNull(savedUpdatedPost);
        assertEquals(postId, savedUpdatedPost.getId());
        assertEquals("Новое название поста", savedUpdatedPost.getTitle());
        assertEquals("newBase64data", savedUpdatedPost.getBase64Image());
        assertEquals("new text content", savedUpdatedPost.getContent());
        assertEquals(0, savedUpdatedPost.getLikes());
    }

    @Test
    void save_whenInvalidPost_ShouldThrowDataIntegrityViolationException() {
        Post postWithoutTitle = new Post(null, "base64data", "text content");
        Post postWithoutImage = new Post("Название поста", null, "text content");
        Post postWithoutContent = new Post("Название поста", "base64data", null);

        assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(postWithoutTitle));
        assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(postWithoutImage));
        assertThrows(DataIntegrityViolationException.class, () -> postRepository.save(postWithoutContent));
    }

    @Test
    void findById_whenPostExist_ShouldReturnPost() {
        Post post = new Post("Название поста", "base64data", "text content");
        long postId = postRepository.save(post);
        Optional<Post> optionalPost = postRepository.findById(postId);

        assertTrue(optionalPost.isPresent());
        Post savedPost = optionalPost.get();
        assertEquals(postId, savedPost.getId());
        assertEquals("Название поста", savedPost.getTitle());
        assertEquals("base64data", savedPost.getBase64Image());
        assertEquals("text content", savedPost.getContent());
        assertEquals(0, savedPost.getLikes());
    }

    @Test
    void findById_whenPostDoesNotExist_ShouldReturnNull() {
        Optional<Post> optionalPost = postRepository.findById(Long.MAX_VALUE);

        assertTrue(optionalPost.isEmpty());
    }

    private Post findPostById(long postId) {
        String query = "select * from myblog.posts where id = ?";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            Post p = new Post();
            p.setId(resultSet.getLong("id"));
            p.setTitle(resultSet.getString("title"));
            p.setBase64Image(resultSet.getString("base_64_image"));
            p.setContent(resultSet.getString("content"));
            p.setLikes(resultSet.getLong("likes"));
            return p;
        }, postId);
    }
}
