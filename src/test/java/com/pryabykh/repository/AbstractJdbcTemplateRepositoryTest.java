package com.pryabykh.repository;

import com.pryabykh.model.Post;
import com.pryabykh.model.PostTag;
import com.pryabykh.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;

public abstract class AbstractJdbcTemplateRepositoryTest {

    protected Long insertPost(JdbcTemplate jdbcTemplate) {
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

    protected Long insertTag(String name, JdbcTemplate jdbcTemplate) {
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

    protected void insertPostTag(Long postId, Long tagId, JdbcTemplate jdbcTemplate) {
        PostTag postTag = new PostTag(postId, tagId);
        String insertSql = """
                insert into myblog.posts_tags (post_id, tag_id) values (?, ?)
                """;

        jdbcTemplate.update(insertSql, postTag.getPostId(), postTag.getTagId());
    }
}
