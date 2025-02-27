package com.pryabykh.repository;

import com.pryabykh.model.Comment;
import com.pryabykh.configuration.H2DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

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

    @Test
    void findAllByPostId_whenCommentsExist_ShouldReturnListOfComments() {
        Long postId = insertPost(jdbcTemplate);
        Comment comment1 = new Comment("Comment content1", postId);
        Comment comment2 = new Comment("Comment content2", postId);
        Comment comment3 = new Comment("Comment content3", postId);

        long commentId1 = commentRepository.save(comment1);
        long commentId2 = commentRepository.save(comment2);
        long commentId3 = commentRepository.save(comment3);

        List<Comment> comments = commentRepository.findAllByPostId(postId);

        assertNotNull(comments);
        assertEquals(3, comments.size());

        Comment savedComment1 = comments.get(2);
        Comment savedComment2 = comments.get(1);
        Comment savedComment3 = comments.get(0);

        assertNotNull(savedComment1);
        assertEquals(commentId1, savedComment1.getId());
        assertEquals("Comment content1", savedComment1.getContent());
        assertEquals(postId, savedComment1.getPostId());

        assertNotNull(savedComment2);
        assertEquals(commentId2, savedComment2.getId());
        assertEquals("Comment content2", savedComment2.getContent());
        assertEquals(postId, savedComment2.getPostId());

        assertNotNull(savedComment3);
        assertEquals(commentId3, savedComment3.getId());
        assertEquals("Comment content3", savedComment3.getContent());
        assertEquals(postId, savedComment3.getPostId());
    }

    @Test
    void deleteById_whenCommentExist_ShouldBeDeleted() {
        Long postId = insertPost(jdbcTemplate);
        Long commentId = insertComment(postId, jdbcTemplate);
        commentRepository.deleteById(commentId);
        assertThrows(EmptyResultDataAccessException.class, () -> findCommentById(postId));
    }

    @Test
    void deleteByPostId_whenCommentExist_ShouldBeDeleted() {
        Long postId = insertPost(jdbcTemplate);
        insertComment(postId, jdbcTemplate);
        insertComment(postId, jdbcTemplate);
        insertComment(postId, jdbcTemplate);
        commentRepository.deleteByPostId(postId);

        List<Comment> commentsByPostId = commentRepository.findAllByPostId(postId);
        assertEquals(0, commentsByPostId.size());
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
