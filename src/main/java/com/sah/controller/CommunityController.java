package com.sah.controller;

import com.sah.dto.requests.PostRequestDTO;
import com.sah.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CommunityController {

    private final PostsService postsService;

    @Autowired
    public CommunityController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping("/community")
    public String showCommunityPage() {
        return "templates/community";
    }

    @PostMapping("/community/post")
    public void post(PostRequestDTO request, Principal principal) {
        postsService.savePost(request, principal);
    }
}
