package com.pryabykh.repository;

import com.pryabykh.dto.Page;
import com.pryabykh.model.Comment;
import com.pryabykh.model.Post;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
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
                select p.*, (select count(*) from myblog.comments c where c.post_id = p.id) as comments_count from myblog.posts p where p.id = ?
                """;
        try {
            Post post = jdbcTemplate.queryForObject(selectSql, (resultSet, rowNum) -> {
                Post p = new Post();
                p.setId(resultSet.getLong("id"));
                p.setTitle(resultSet.getString("title"));
                p.setBase64Image(resultSet.getString("base_64_image"));
                p.setContent(resultSet.getString("content"));
                p.setLikes(resultSet.getLong("likes"));
                p.setCommentsCount(resultSet.getLong("comments_count"));
                return p;
            }, postId);
            return Optional.ofNullable(post);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    @Override
    public int countByTag(String tag) {
        String selectSql = "select count(*) from myblog.posts" + fetchWhereStatementForPaging(tag);
        if (tag != null) {
            return jdbcTemplate.queryForObject(selectSql, Integer.class, tag);
        } else {
            return jdbcTemplate.queryForObject(selectSql, Integer.class);
        }
    }

    @Override
    public List<Post> findAllByTag(String tag, int pageNumber, int pageSize) {
        String selectSql = "select p.*, (select count(*) from myblog.comments c where c.post_id = p.id) as comments_count from myblog.posts p" +
                fetchWhereStatementForPaging(tag) +
                " order by p.id desc limit ? offset ?";

        RowMapper<Post> rowMapper = (resultSet, rowNum) -> {
            Post p = new Post();
            p.setId(resultSet.getLong("id"));
            p.setTitle(resultSet.getString("title"));
            p.setBase64Image(resultSet.getString("base_64_image"));
            p.setContent(resultSet.getString("content"));
            p.setLikes(resultSet.getLong("likes"));
            p.setCommentsCount(resultSet.getLong("comments_count"));
            return p;
        };
        int offset = pageNumber * pageSize;
        if (tag != null) {
            return jdbcTemplate.query(selectSql, rowMapper, tag, pageSize, offset);
        } else {
            return jdbcTemplate.query(selectSql, rowMapper, pageSize, offset);
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

    private String fetchWhereStatementForPaging(String tag) {
        if (tag != null) {
            return """
                 where id in
                (select distinct pt.post_id from myblog.tags t join myblog.posts_tags pt on pt.tag_id = t.id where t.content = ?)
                """;
        } else {
            return "";
        }
    }
}
