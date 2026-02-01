package com.sah.controller;

import com.sah.service.ChessLobbyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ChessLobbyController {

    private final ChessLobbyService chessLobbyService;

    public ChessLobbyController(ChessLobbyService chessLobbyService) {
        this.chessLobbyService = chessLobbyService;
    }

    @PostMapping("/play/create")
    public String createLobby() {
        String lobby_Id = chessLobbyService.GenerateRandomLobbyId();
        return "redirect:/play/"+lobby_Id;
    }

    @GetMapping("/play/{lobby_Id}")
    public String showLobby(@PathVariable String lobby_Id) {
        return "game/play";
    }
}
