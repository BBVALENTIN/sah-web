package com.sah.controller.api;

import com.sah.dto.requests.PostRequestDTO;
import com.sah.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/community")
public class CommunityApiController {
    private final PostsService postsService;

    public CommunityApiController(PostsService postsService) {
        this.postsService = postsService;
    }

    @PostMapping("/post")
    public void post(PostRequestDTO request, Principal principal) {
        postsService.savePost(request, principal);
    }
}
