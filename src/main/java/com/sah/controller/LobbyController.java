package com.sah.controller;

import com.sah.dto.lobbyDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LobbyController {

    @GetMapping("/lobbies")
    public String getLobbyPage() {
        return "/game/lobby";
    }

    @GetMapping("/api/lobbies")
    public List<lobbyDTO> getLobbies() {

    }
}
