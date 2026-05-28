package com.sah.controller;

import com.sah.service.TwitchService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class IndexController {

    private final TwitchService twitchService;

    public IndexController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @GetMapping("/")
    public String showIndex(Model model)
    {
        model.addAttribute("stream", twitchService.getMostPopularChessStream());
        return "index/index";
    }
}
