package com.pryabykh.myblog.controller;

import com.pryabykh.myblog.dto.CommentDto;
import com.pryabykh.myblog.dto.Page;
import com.pryabykh.myblog.dto.PostDto;
import com.pryabykh.myblog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/create-post")
    public String createPost(@ModelAttribute PostDto postDto) {
        blogService.createPost(postDto);
        return "redirect:/"; // Redirect to the feed page
    }

    @PostMapping("/update-post/{id}")
    public String updatatePost(@PathVariable("id") Long id,
                               @ModelAttribute PostDto postDto) {
        blogService.updatePost(id, postDto);
        return "redirect:/post/" + id;
    }

    @GetMapping("/delete-post/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        blogService.deletePost(id);
        return "redirect:/"; // Redirect to the feed page
    }

    @GetMapping("/like-post/{id}")
    public String likePost(@PathVariable("id") Long id) {
        blogService.likePost(id);
        return "redirect:/post/" + id;
    }

    @PostMapping("/create-comment/{postId}")
    public String createComment(@PathVariable("postId") Long postId,
                                @ModelAttribute CommentDto commentDto) {
        blogService.addComment(postId, commentDto);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/update-comment/{postId}/{commentId}")
    public String updateComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @ModelAttribute CommentDto commentDto) {
        blogService.updateComment(commentId, commentDto);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/delete-comment/{postId}/{commentId}")
    public String deleteComment(@PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId) {
        blogService.deleteComment(commentId);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/create-post-form")
    public String showCreatePostForm() {
        return "create-post";
    }

    @GetMapping("/update-post-form/{id}")
    public String showUpdatePostForm(@PathVariable("id") Long id, Model model) {
        PostDto post = blogService.findPostById(id);
        model.addAttribute("post", post);
        return "update-post";
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {
        PostDto post = blogService.findPostById(id);
        model.addAttribute("post", post);
        return "post";
    }
}
