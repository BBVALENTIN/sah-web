package com.sah.controller;

import com.sah.dto.misc.LobbyDTO;
import com.sah.dto.misc.loggedUser;
import com.sah.service.ChessLobbyService;
import com.sah.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/info")
public class InfoController {

    private final ChessLobbyService chessLobbyService;
    CustomUserDetailsService customUserDetailsService;

    public InfoController(CustomUserDetailsService customUserDetailsService, ChessLobbyService chessLobbyService) {
        this.customUserDetailsService = customUserDetailsService;
        this.chessLobbyService = chessLobbyService;
    }

    @SendTo("/topic/public")
    @GetMapping("/lobby")
    public LobbyDTO getLobbyInfo(HttpSession session) {
        String lobbyId = chessLobbyService.getLobbyIdFromSession(session.getId());
        if(lobbyId == null) {
            throw new RuntimeException("User is not in a lobby");
        }

        LobbyDTO currentLobby = chessLobbyService.getLobbyDTO(lobbyId);

        return currentLobby;
    }

    @GetMapping("/user")
    public loggedUser getCurrentUser(Principal principal) {
        if(principal == null) {
            throw new RuntimeException("Principal is null");
        }

        return customUserDetailsService.loadInfo(principal);
    }
}
