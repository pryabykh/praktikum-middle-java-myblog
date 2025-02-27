package com.pryabykh.controller;

import com.pryabykh.configuration.WebConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringJUnitConfig(classes = {WebConfiguration.class})
public class BlogControllerTest {

    @Test
    void getPosts_shouldReturnHtmlWithPosts() {

    }
}
