package com.sah.controller.pages;

import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.service.PostsService;
import com.sah.service.ProfileService;
import com.sah.service.TwitchService;
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
        model.addAttribute("userMiscInfo", profileService.returnMiscInfo(principal.getName()));
        model.addAttribute("posts", postsService.returnPosts(principal));
        model.addAttribute("resultString", profileService.getLastGamesOutcome(principal));
        return "index/index";
    }
}
