package com.sah.controller.pages;

import com.sah.security.CurrentUserProvider;
import com.sah.service.lobby.ChessLobbyService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class LobbyPageController {

    private final ChessLobbyService chessLobbyService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/lobbies")
    public String getLobbyPage() {
        return "/game/lobby";
    }

    @GetMapping("/play={lobbyId}")
    public String showLobby(@PathVariable String lobbyId, HttpSession session, Model model) {
        chessLobbyService.registerPlayer(session.getId(), lobbyId);
        model.addAttribute("currentUsername", currentUserProvider.get().getUsername());
        return "game/play";
    }
}
