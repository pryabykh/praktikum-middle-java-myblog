package com.pryabykh.myblog.repository;

import com.pryabykh.myblog.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@ActiveProfiles("test")
@Import({JdbcTemplateTagRepository.class})
public class JdbcTemplateTagRepositoryTest extends AbstractJdbcTemplateRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplateTagRepository tagRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
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

    @Test
    void findById_whenTagExist_ShouldReturnTag() {
        Tag tag = new Tag("Tag");
        long tagId = tagRepository.save(tag);
        Optional<Tag> optionalTag = tagRepository.findById(tagId);

        assertTrue(optionalTag.isPresent());
        Tag savedTag = optionalTag.get();
        assertEquals(tagId, savedTag.getId());
        assertEquals("Tag", savedTag.getContent());
    }

    @Test
    void findById_whenTagDoesNotExist_ShouldReturnNull() {
        Optional<Tag> optionalTag = tagRepository.findById(Long.MAX_VALUE);

        assertTrue(optionalTag.isEmpty());
    }

    @Test
    void findByContent_whenTagExist_ShouldReturnTag() {
        Tag tag = new Tag("Tag");
        long tagId = tagRepository.save(tag);
        Optional<Tag> optionalTag = tagRepository.findByContent("Tag");

        assertTrue(optionalTag.isPresent());
        Tag savedTag = optionalTag.get();
        assertEquals(tagId, savedTag.getId());
        assertEquals("Tag", savedTag.getContent());
    }

    @Test
    void findByContent_whenTagDoesNotExist_ShouldReturnNull() {
        Optional<Tag> optionalTag = tagRepository.findByContent("test non existed tag");

        assertTrue(optionalTag.isEmpty());
    }

    @Test
    void findAllByPostId_whenTagsExist_ShouldReturnListOfTags() {
        Long postId = insertPost(jdbcTemplate);
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        Tag tag3 = new Tag("tag3");

        long tag1Id = tagRepository.save(tag1);
        long tag2Id = tagRepository.save(tag2);
        long tag3Id = tagRepository.save(tag3);

        insertPostTag(postId, tag1Id, jdbcTemplate);
        insertPostTag(postId, tag2Id, jdbcTemplate);
        insertPostTag(postId, tag3Id, jdbcTemplate);

        List<Tag> tags = tagRepository.findAllByPostId(postId);

        assertNotNull(tags);
        assertEquals(3, tags.size());

        Tag savedTag1 = tags.get(0);
        Tag savedTag2 = tags.get(1);
        Tag savedTag3 = tags.get(2);

        assertNotNull(savedTag1);
        assertEquals(tag1Id, savedTag1.getId());
        assertEquals("tag1", savedTag1.getContent());

        assertNotNull(savedTag2);
        assertEquals(tag2Id, savedTag2.getId());
        assertEquals("tag2", savedTag2.getContent());

        assertNotNull(savedTag3);
        assertEquals(tag3Id, savedTag3.getId());
        assertEquals("tag3", savedTag3.getContent());
    }

    @Test
    void findAllByPostIdIn_whenTagsExist_ShouldReturnListOfTags() {
        Long postId1 = insertPost(jdbcTemplate);
        Long postId2 = insertPost(jdbcTemplate);
        Long postId3 = insertPost(jdbcTemplate);

        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        Tag tag3 = new Tag("tag3");
        Tag tag4 = new Tag("tag4");

        long tag1Id = tagRepository.save(tag1);
        long tag2Id = tagRepository.save(tag2);
        long tag3Id = tagRepository.save(tag3);
        long tag4Id = tagRepository.save(tag4);

        insertPostTag(postId1, tag1Id, jdbcTemplate);
        insertPostTag(postId1, tag2Id, jdbcTemplate);
        insertPostTag(postId2, tag3Id, jdbcTemplate);
        insertPostTag(postId3, tag4Id, jdbcTemplate);

        List<Tag> tags = tagRepository.findAllByPostIdIn(List.of(postId1, postId2));

        assertNotNull(tags);
        assertEquals(3, tags.size());

        Tag savedTag1 = tags.get(0);
        Tag savedTag2 = tags.get(1);
        Tag savedTag3 = tags.get(2);

        assertNotNull(savedTag1);
        assertEquals(tag1Id, savedTag1.getId());
        assertEquals("tag1", savedTag1.getContent());

        assertNotNull(savedTag2);
        assertEquals(tag2Id, savedTag2.getId());
        assertEquals("tag2", savedTag2.getContent());

        assertNotNull(savedTag3);
        assertEquals(tag3Id, savedTag3.getId());
        assertEquals("tag3", savedTag3.getContent());
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
