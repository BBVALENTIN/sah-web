package com.sah.controller;

import com.sah.dto.JoinLobbyRequest;
import com.sah.dto.lobbyDTO;
import com.sah.enums.LobbyType;
import com.sah.service.ChessLobbyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/lobbies/{desiredLobby}")
    @ResponseBody
    public List<lobbyDTO> getLobbies(@PathVariable LobbyType desiredLobby) {
        return chessLobbyService.getAllDesiredLobbies(desiredLobby);
    }

    @PostMapping("/api/joinLobby")
    @ResponseBody
    public String joinLobby(@RequestBody JoinLobbyRequest request) {
        if(chessLobbyService.checkJoinable(request.getLobbyId()) == false) {
            return "Lobby is full";
        }
        chessLobbyService.assignLobbyPlayer(chessLobbyService.getLobbyFromId(request.getLobbyId()), request.getUsername());
        return "play="+request.getLobbyId();
    }
}
