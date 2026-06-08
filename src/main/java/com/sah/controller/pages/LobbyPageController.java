package com.sah.controller.pages;

import com.sah.service.ChessLobbyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LobbyPageController {

    private final ChessLobbyService chessLobbyService;

    public LobbyPageController(ChessLobbyService cls) {
        chessLobbyService = cls;
    }

    @GetMapping("/lobbies")
    public String getLobbyPage() {
        return "/game/lobby";
    }

    @GetMapping("/play={lobbyId}")
    public String showLobby(@PathVariable String lobbyId, HttpSession session) {
        chessLobbyService.registerPlayer(session.getId(), lobbyId);
        return "game/play";
    }
}
