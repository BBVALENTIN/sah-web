package com.sah.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/game")
    public String gamePage() {
        return "game"; // se caută game.html în src/main/resources/templates/
    }
}