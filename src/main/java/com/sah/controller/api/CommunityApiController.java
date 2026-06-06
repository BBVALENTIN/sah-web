package com.sah.controller.api;

import com.sah.dto.misc.CommentRequestDTO;
import com.sah.dto.misc.PostDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.entity.Posts;
import com.sah.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/likePost/{postId}")
    public void likePost(@PathVariable Long postId, Principal principal) {
        postsService.likePost(postId, principal);
    }

    @PostMapping("/comment")
    public ResponseEntity<?> comment(@RequestBody CommentRequestDTO req, Principal principal) {
        postsService.publishComment(req.getPostId(), req.getContent(), principal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deletePost/{postId}")
    public void deletePost(@PathVariable Long postId, Principal principal) {
        postsService.deletePost(postId, principal);
    }

    @PostMapping("/deleteComment/{commentId}")
    public void deleteComment(@PathVariable Long commentId, Principal principal) {
        postsService.deleteComment(commentId, principal);
    }

    @PostMapping("/likeComment/{commentId}")
    public void likeComment(@PathVariable Long commentId, Principal principal) { postsService.likeComment(commentId, principal);}
}
