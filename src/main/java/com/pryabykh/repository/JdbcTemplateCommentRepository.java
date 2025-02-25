package com.pryabykh.repository;

import com.pryabykh.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcTemplateCommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Comment comment) {
        if (comment.getId() == null) {
            return insert(comment);
        } else {
            return update(comment);
        }
    }

    @Override
    public List<Comment> findAllByPostId(long postId) {
        String selectSql = """
                select * from myblog.comments where post_id = ? order by id desc
                """;

        return jdbcTemplate.query(selectSql, (resultSet, rowNum) -> {
            Comment c = new Comment();
            c.setId(resultSet.getLong("id"));
            c.setContent(resultSet.getString("content"));
            c.setPostId(resultSet.getLong("post_id"));
            return c;
        }, postId);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from myblog.comments where id = ?", id);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update("delete from myblog.comments where post_id = ?", postId);
    }

    private long insert(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String insertSql = """
                insert into myblog.comments (content, post_id) values (?, ?)
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getContent());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id != null) {
            return id.longValue();
        } else {
            throw new IllegalArgumentException("Не удалось получить id при создании Comment");
        }
    }

    private long update(Comment comment) {
        String updateSql = """
                update myblog.comments set content = ? where id = ?
                """;

        jdbcTemplate.update(updateSql, comment.getContent(), comment.getId());
        return comment.getId();
    }
}
