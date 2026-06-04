package com.sah.controller.pages;

import com.sah.controller.ProfileController;
import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.dto.requests.PostRequestDTO;
import com.sah.service.PostsService;
import com.sah.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        ProfileInfoDTO profileInfoDTO = profileService.loadProfileInfo(principal.getName());
        model.addAttribute("username", profileInfoDTO.getUsername());
        model.addAttribute("useravatar", profileInfoDTO.getAvatar());
        model.addAttribute("posts", postsService.returnPosts(principal));
        return "community/community";
    }

    @GetMapping("/post/{postId}")
    public String showPostComments(@PathVariable Long postId, Model model, Principal principal)
    {
        model.addAttribute("post", postsService.returnPost(postId, principal));
        model.addAttribute("comments", postsService.getComments(postId, principal));
        return "community/post";
    }

}
