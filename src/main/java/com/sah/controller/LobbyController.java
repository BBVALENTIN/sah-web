package com.sah.controller;

import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

    public String getLobbyPage() {
        return "/game/lobby";
    }
}
