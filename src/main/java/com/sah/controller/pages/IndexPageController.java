package com.sah.controller.pages;

import com.sah.config.AppConstants;
import com.sah.service.community.PostsService;
import com.sah.service.user.ProfileService;
import com.sah.service.misc.TwitchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class IndexPageController {

    private final TwitchService twitchService;
    private final ProfileService profileService;
    private final PostsService postsService;

    public IndexPageController(TwitchService twitchService, ProfileService profileService, PostsService postsService) {
        this.twitchService = twitchService;
        this.profileService = profileService;
        this.postsService = postsService;
    }

    @GetMapping("/")
    public String showIndex(Model model, Principal principal)
    {
        model.addAttribute("stream", twitchService.getMostPopularChessStream());
        model.addAttribute("userMiscInfo", profileService.returnMiscInfo());
        model.addAttribute("posts", postsService.returnPostsPage(AppConstants.DEFAULT_PAGE, AppConstants.POSTS_PER_PAGE));
        model.addAttribute("resultString", profileService.getLastGamesOutcome(principal));
        return "index/index";
    }
}
