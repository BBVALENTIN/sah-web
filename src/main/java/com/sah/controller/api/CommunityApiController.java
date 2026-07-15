package com.sah.controller.api;

import com.sah.config.AppConstants;
import com.sah.dto.misc.CommentRequestDTO;
import com.sah.dto.misc.PostDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.service.community.PostsService;
import org.springframework.http.ResponseEntity;
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
    public void post(@RequestBody PostRequestDTO request) {
        if(request.getContent().isBlank() || request.getContent() == null) {
            throw new RuntimeException("The post should contain at least one character");
        }
        postsService.savePost(request);
    }

    @GetMapping("/getPosts")
    public List<PostDTO> getPosts() {
        return postsService.returnPostsPage(AppConstants.DEFAULT_PAGE, AppConstants.POSTS_PER_PAGE);
    }

    @PostMapping("/likePost/{postId}")
    public void likePost(@PathVariable Long postId) {
        postsService.likePost(postId);
    }

    @PostMapping("/comment")
    public ResponseEntity<?> comment(@RequestBody CommentRequestDTO req) {
        postsService.publishComment(req.getPostId(), req.getContent());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deletePost/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postsService.deletePost(postId);
    }

    @PostMapping("/deleteComment/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        postsService.deleteComment(commentId);
    }

    @PostMapping("/likeComment/{commentId}")
    public void likeComment(@PathVariable Long commentId) { postsService.likeComment(commentId);}
}
