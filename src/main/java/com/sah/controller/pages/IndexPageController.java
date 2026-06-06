package com.sah.controller.pages;

import com.sah.service.TwitchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexPageController {

    private final TwitchService twitchService;

    public IndexPageController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @GetMapping("/")
    public String showIndex(Model model)
    {
        model.addAttribute("stream", twitchService.getMostPopularChessStream());
        return "index/index";
    }
}
