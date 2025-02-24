package com.pryabykh.repository;

import com.pryabykh.model.Tag;
import configuration.H2DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(classes = {H2DataSourceConfiguration.class, JdbcTemplateTagRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcTemplateTagRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplateTagRepository tagRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
    }

    @Test
    void save_whenValidTagWithoutId_ShouldInsertTagToDatabase() {
        Tag tag = new Tag("Tag1");

        long tagId = tagRepository.save(tag);

        Tag savedTag = findTagById(tagId);

        assertNotNull(savedTag);
        assertEquals(tagId, savedTag.getId());
        assertEquals("Tag1", savedTag.getContent());
    }

    @Test
    void save_whenExistedTagWithoutId_ShouldThrowDuplicateKeyException() {
        Tag tag = new Tag("Tag1");

        tagRepository.save(tag);

        assertThrows(DuplicateKeyException.class, () -> tagRepository.save(tag));
    }

    @Test
    void save_whenValidTagWithId_ShouldUpdateTagToDatabase() {
        Tag tag = new Tag("Tag1");
        long tagId = tagRepository.save(tag);

        Tag udpdatedTag = new Tag(tagId, "Tag2");
        tagRepository.save(udpdatedTag);

        Tag savedUpdatedTag = findTagById(tagId);

        assertNotNull(savedUpdatedTag);
        assertEquals(tagId, savedUpdatedTag.getId());
        assertEquals("Tag2", savedUpdatedTag.getContent());
    }

    @Test
    void save_whenInvalidTag_ShouldThrowDataIntegrityViolationException() {
        Tag tagWithoutContent = new Tag(null);

        assertThrows(DataIntegrityViolationException.class, () -> tagRepository.save(tagWithoutContent));
    }

    private Tag findTagById(long postId) {
        String query = "select * from myblog.tags where id = ?";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            Tag t = new Tag();
            t.setId(resultSet.getLong("id"));
            t.setContent(resultSet.getString("content"));
            return t;
        }, postId);
    }
}
