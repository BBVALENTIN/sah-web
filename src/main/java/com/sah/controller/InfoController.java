package com.sah.controller;

import com.sah.dto.LobbyDTO;
import com.sah.dto.loggedUser;
import com.sah.service.ChessLobbyService;
import com.sah.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.RuntimeErrorException;
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

    @GetMapping("/lobby")
    public LobbyDTO getLobbyInfo(Principal principal, HttpSession session) {
        if(principal == null) {
            throw new RuntimeException("Principal is null");
        }
        String lobbyId = chessLobbyService.getLobbyIdFromSession(session.getId());
        if(lobbyId == null) {
            throw new RuntimeException("User is not in a lobby");
        }

        LobbyDTO currentLobby = chessLobbyService.getLobbyDTO(lobbyId);
        currentLobby.setLoggedUsername(customUserDetailsService.loadInfo(principal));

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
