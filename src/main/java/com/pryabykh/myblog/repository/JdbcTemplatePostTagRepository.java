package com.pryabykh.myblog.repository;

import com.pryabykh.myblog.model.PostTag;
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

    @Override
    public void deleteByPostId(Long postId) {
        String deleteSql = """
                delete from myblog.posts_tags where post_id = ?
                """;

        jdbcTemplate.update(deleteSql, postId);
    }
}
