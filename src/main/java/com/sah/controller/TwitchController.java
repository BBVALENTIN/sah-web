package com.sah.controller;

import com.sah.service.TwitchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TwitchController { // THIS IS A SAMPLE CONTROLLER*

    private final TwitchService twitchService;

    public TwitchController(TwitchService twitchService) {
        this.twitchService = twitchService;
    }

    @GetMapping("/streamers")
    public String chessStreams(Model model) {
        model.addAttribute("streams", twitchService.getChessStreams());
        return "streams/chess-streams";
    }
}
