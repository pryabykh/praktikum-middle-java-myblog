package com.pryabykh.controller;

import com.pryabykh.configuration.WebConfiguration;
import com.pryabykh.repository.AbstractJdbcTemplateRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebAppConfiguration
@SpringJUnitConfig(classes = {WebConfiguration.class})
public class BlogControllerTest extends AbstractJdbcTemplateRepositoryTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        jdbcTemplate.execute("DELETE FROM myblog.posts_tags");
        jdbcTemplate.execute("DELETE FROM myblog.comments");
        jdbcTemplate.execute("DELETE FROM myblog.posts");
        jdbcTemplate.execute("DELETE FROM myblog.tags");
    }

    @Test
    void getPosts_shouldReturnHtmlWithPosts() throws Exception {
        mockMvc.perform(get("/?tag=some_ag"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalElements"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("tag"));
    }

    @Test
    void showPost_shouldReturnHtmlWithPost() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(get("/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void createPost_shouldCreatePostAndRedirect() throws Exception {
        mockMvc.perform(post("/create-post")
                        .param("title", "test")
                        .param("base64Image", "test")
                        .param("content", "test")
                        .param("tags", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void updatePost_shouldUpdateAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(post("/update-post/" + postId)
                        .param("title", "test")
                        .param("base64Image", "test")
                        .param("content", "test")
                        .param("tags", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));
    }

    @Test
    void deletePost_shouldDeletePostAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(get("/delete-post/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void likePost_shouldLikePostAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(get("/like-post/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));
    }

    @Test
    void createComment_shouldCreateCommentAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(post("/create-comment/" + postId)
                        .param("content", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));
    }

    @Test
    void updateComment_shouldUpdateCommentAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        Long commentId = insertComment(postId, jdbcTemplate);
        mockMvc.perform(post("/update-comment/" + postId + "/" + commentId)
                        .param("content", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));
    }

    @Test
    void deleteComment_shouldDeleteCommentAndRedirect() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        Long commentId = insertComment(postId, jdbcTemplate);
        mockMvc.perform(get("/delete-comment/" + postId + "/" + commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/" + postId));
    }

    @Test
    void showCreatePostForm_shouldShowCreatePostForm() throws Exception {
        mockMvc.perform(get("/create-post-form"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("create-post"));
    }

    @Test
    void showUpdatePostForm_shouldShowUpdatePostForm() throws Exception {
        Long postId = insertPost(jdbcTemplate);
        mockMvc.perform(get("/update-post-form/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("update-post"))
                .andExpect(model().attributeExists("post"));
    }
}
