package com.pryabykh.repository;

import com.pryabykh.model.Post;
import com.pryabykh.model.PostTag;
import com.pryabykh.model.Tag;
import configuration.H2DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = {H2DataSourceConfiguration.class, JdbcTemplatePostTagRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class JdbcTemplatePostTagRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplatePostTagRepository postTagRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
    }

    @Test
    void save_whenValidPostTag_ShouldInsertPostTagToDatabase() {
        Long postId = insertPost();
        Long tagId = insertTag("Tag");

        PostTag postTag = new PostTag(postId, tagId);
        postTagRepository.save(postTag);

        PostTag savedPostTag = findPostTag();

        assertNotNull(savedPostTag);
        assertEquals(postId, savedPostTag.getPostId());
        assertEquals(tagId, savedPostTag.getTagId());
    }

    @Test
    void deleteByPostId_whenEntitiesExist_ShouldDeleteThemByPostId() {
        Long postId = insertPost();
        Long tagId1 = insertTag("Tag1");
        Long tagId2 = insertTag("Tag2");
        Long tagId3 = insertTag("Tag3");

        PostTag postTag1 = new PostTag(postId, tagId1);
        PostTag postTag2 = new PostTag(postId, tagId2);
        PostTag postTag3 = new PostTag(postId, tagId3);

        postTagRepository.save(postTag1);
        postTagRepository.save(postTag2);
        postTagRepository.save(postTag3);

        assertEquals(3L, countAll());

        postTagRepository.deleteByPostId(postId);

        assertEquals(0L, countAll());
    }

    private PostTag findPostTag() {
        String query = "select * from myblog.posts_tags";
        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) -> {
            PostTag pt = new PostTag();
            pt.setPostId(resultSet.getLong("post_id"));
            pt.setTagId(resultSet.getLong("tag_id"));
            return pt;
        });
    }

    private Long countAll() {
        String query = "select count(*) from myblog.posts_tags";
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    private Long insertPost() {
        Post post = new Post("title", "image", "content");
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String insertSql = """
                insert into myblog.posts (title, base_64_image, content) values (?, ?, ?)
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getBase64Image());
            ps.setString(3, post.getContent());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id != null) {
            return id.longValue();
        } else {
            throw new IllegalArgumentException("Не удалось получить id при создании Post");
        }
    }

    private Long insertTag(String name) {
        Tag tag = new Tag(name);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String insertSql = """
                insert into myblog.tags (content) values (?)
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tag.getContent());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id != null) {
            return id.longValue();
        } else {
            throw new IllegalArgumentException("Не удалось получить id при создании Tag");
        }
    }
}
