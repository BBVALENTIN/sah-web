package com.sah.controller.pages;

import com.sah.service.community.PostsService;
import com.sah.service.user.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class CommunityPageController {

    private final PostsService postsService;
    private ProfileService profileService;

    public CommunityPageController(ProfileService profileService, PostsService postsService) {
        this.profileService = profileService;
        this.postsService = postsService;
    }

    @GetMapping("/community")
    public String showCommunityPage(Model model, Principal principal) {
        model.addAttribute("userMiscInfo", profileService.returnMiscInfo(principal.getName()));
        model.addAttribute("posts", postsService.returnPosts(principal));
        return "community/community";
    }

    @GetMapping("/post/{postId}")
    public String showPostComments(@PathVariable Long postId, Model model, Principal principal)
    {
        model.addAttribute("userMiscInfo", profileService.returnMiscInfo(principal.getName()));
        model.addAttribute("post", postsService.returnPost(postId, principal));
        model.addAttribute("comments", postsService.getComments(postId, principal));
        return "community/post";
    }
}
