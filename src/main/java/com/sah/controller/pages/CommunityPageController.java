package com.sah.controller.pages;

import com.sah.config.AppConstants;
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
    public String showCommunityPage(Model model) {
        model.addAttribute("posts", postsService.returnPostsPage(AppConstants.DEFAULT_PAGE, AppConstants.POSTS_PER_PAGE));
        return "community/community";
    }

    @GetMapping("/post/{postId}")
    public String showPostComments(@PathVariable Long postId, Model model)
    {
        model.addAttribute("userMiscInfo", profileService.returnMiscInfo());
        model.addAttribute("post", postsService.returnPost(postId));
        model.addAttribute("comments", postsService.getComments(postId));
        return "community/post";
    }
}
