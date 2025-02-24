package com.pryabykh.repository;

import com.pryabykh.model.Comment;
import configuration.H2DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(classes = {H2DataSourceConfiguration.class, JdbcTemplateCommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcTemplateCommentRepositoryTest extends AbstractJdbcTemplateRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplateCommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
    }

    @Test
    void save_whenValidCommentWithoutId_ShouldInsertCommentToDatabase() {
        Long postId = insertPost(jdbcTemplate);
        Comment comment = new Comment("Comment content", postId);

        long commentId = commentRepository.save(comment);

        Comment savedComment = findCommentById(commentId);

        assertNotNull(savedComment);
        assertEquals(commentId, savedComment.getId());
        assertEquals("Comment content", savedComment.getContent());
        assertEquals(postId, savedComment.getPostId());
    }

    @Test
    void save_whenValidCommentWithId_ShouldUpdateCommentToDatabase() {
        Long postId = insertPost(jdbcTemplate);
        Comment comment = new Comment("Comment content", postId);
        long commentId = commentRepository.save(comment);

        Comment updatedComment = new Comment(commentId, "Updated Comment content", postId);

        commentRepository.save(updatedComment);

        Comment savedUpdatedComment = findCommentById(commentId);

        assertNotNull(savedUpdatedComment);
        assertEquals(commentId, savedUpdatedComment.getId());
        assertEquals("Updated Comment content", savedUpdatedComment.getContent());
        assertEquals(postId, savedUpdatedComment.getPostId());
    }

    @Test
    void save_whenInvalidTag_ShouldThrowDataIntegrityViolationException() {
        Long postId = insertPost(jdbcTemplate);
        Comment commentWithoutContent = new Comment(null, postId);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(commentWithoutContent));
    }

    private Comment findCommentById(long postId) {
        String query = "select * from myblog.comments where id = ?";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            Comment c = new Comment();
            c.setId(resultSet.getLong("id"));
            c.setContent(resultSet.getString("content"));
            c.setPostId(resultSet.getLong("post_id"));
            return c;
        }, postId);
    }
}
