package com.sah.controller;

import com.sah.dto.lobbyDTO;
import com.sah.enums.LobbyType;
import com.sah.service.ChessLobbyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Stream;

@Controller
public class LobbyController {

    private final ChessLobbyService chessLobbyService;

    public LobbyController(ChessLobbyService chessLobbyService) {
        this.chessLobbyService = chessLobbyService;
    }

    @GetMapping("/lobbies")
    public String getLobbyPage() {
        return "/game/lobby";
    }

    @GetMapping("/api/lobbies")
    public Stream<lobbyDTO> getLobbies(LobbyType desiredLobby) {
        return chessLobbyService.getAllDesiredLobbies(desiredLobby);
    }
}
