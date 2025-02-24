package com.pryabykh.repository;

import com.pryabykh.model.Post;
import com.pryabykh.model.Tag;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class JdbcTemplateTagRepository implements TagRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Tag tag) {
        if (tag.getId() == null) {
            return insert(tag);
        } else {
            return update(tag);
        }
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        String selectSql = """
                select * from myblog.tags where id = ?
                """;
        try {
            Tag tag = jdbcTemplate.queryForObject(selectSql, (resultSet, rowNum) -> {
                Tag t = new Tag();
                t.setId(resultSet.getLong("id"));
                t.setContent(resultSet.getString("content"));
                return t;
            }, tagId);
            return Optional.ofNullable(tag);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    private long insert(Tag tag) {
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

    private long update(Tag tag) {
        String updateSql = """
                update myblog.tags set content = ? where id = ?
                """;

        jdbcTemplate.update(updateSql, tag.getContent(), tag.getId());
        return tag.getId();
    }
}
