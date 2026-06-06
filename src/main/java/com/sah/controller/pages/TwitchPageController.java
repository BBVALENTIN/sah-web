package com.sah.controller.pages;

import com.sah.service.TwitchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TwitchPageController { // THIS IS A SAMPLE CONTROLLER*

    private final TwitchService twitchService;

    public TwitchPageController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @GetMapping("/streamers")
    public String chessStreams(Model model) {
        model.addAttribute("streams", twitchService.getChessStreams());
        return "streams/chess-streams";
    }
}
