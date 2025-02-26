package com.pryabykh.controller;

import com.pryabykh.dto.Page;
import com.pryabykh.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public String posts(@RequestParam(value = "page", defaultValue = "1") int currentPage,
                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                        @RequestParam(value = "tag", required = false) String tag,
                        Model model) {
        Page page = blogService.findAllPosts(tag, currentPage - 1, pageSize);
        model.addAttribute("posts", page.getContent());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("tag", tag);

        return "posts";
    }
}
