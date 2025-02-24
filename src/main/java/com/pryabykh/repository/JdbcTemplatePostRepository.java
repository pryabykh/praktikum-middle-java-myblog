package com.pryabykh.repository;

import com.pryabykh.model.Post;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class JdbcTemplatePostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Post post) {
        if (post.getId() == null) {
            return insert(post);
        } else {
            return update(post);
        }
    }

    @Override
    public Optional<Post> findById(Long postId) {
        String selectSql = """
                select * from myblog.posts where id = ?
                """;
        try {
            Post post = jdbcTemplate.queryForObject(selectSql, (resultSet, rowNum) -> {
                Post p = new Post();
                p.setId(resultSet.getLong("id"));
                p.setTitle(resultSet.getString("title"));
                p.setBase64Image(resultSet.getString("base_64_image"));
                p.setContent(resultSet.getString("content"));
                p.setLikes(resultSet.getLong("likes"));
                return p;
            }, postId);
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    private long insert(Post post) {
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

    private long update(Post post) {
        String updateSql = """
                update myblog.posts set title = ?, base_64_image = ?, content = ? where id = ?
                """;

        jdbcTemplate.update(updateSql, post.getTitle(), post.getBase64Image(), post.getContent(), post.getId());
        return post.getId();
    }
}
