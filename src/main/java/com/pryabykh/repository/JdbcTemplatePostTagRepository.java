package com.pryabykh.repository;

import com.pryabykh.model.PostTag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplatePostTagRepository implements PostTagRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePostTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(PostTag postTag) {
        String insertSql = """
                insert into myblog.posts_tags (post_id, tag_id) values (?, ?)
                """;

        jdbcTemplate.update(insertSql, postTag.getPostId(), postTag.getTagId());
    }
}
