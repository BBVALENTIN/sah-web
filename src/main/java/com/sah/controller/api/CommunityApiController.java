package com.sah.controller.api;

import com.sah.dto.misc.PostDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.entity.Posts;
import com.sah.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityApiController {
    private final PostsService postsService;

    public CommunityApiController(PostsService postsService) {
        this.postsService = postsService;
    }

    @PostMapping("/post")
    public void post(@RequestBody PostRequestDTO request, Principal principal) {
        if(request.getContent().isBlank() || request.getContent() == null) {
            throw new RuntimeException("The post should contain at least one character");
        }
        postsService.savePost(request, principal);
    }

    @GetMapping("/getPosts")
    public List<PostDTO> getPosts(Principal principal) {
        return postsService.returnPosts(principal);
    }
}
