package com.sah.controller;

import com.sah.dto.JoinLobbyRequest;
import com.sah.dto.LobbyDTO;
import com.sah.entity.Chess_Lobby;
import com.sah.enums.LobbyType;
import com.sah.repository.LobbyRepository;
import com.sah.service.ChessLobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LobbyController {

    private final ChessLobbyService chessLobbyService;
    private final LobbyRepository lobbyRepository;

    public LobbyController(ChessLobbyService chessLobbyService, LobbyRepository lobbyRepository) {
        this.chessLobbyService = chessLobbyService;
        this.lobbyRepository = lobbyRepository;
    }

    @GetMapping("/lobbies")
    public String getLobbyPage() {
        return "/game/lobby";
    }

    @GetMapping("/api/lobbies/{desiredLobby}")
    @ResponseBody
    public List<LobbyDTO> getLobbies(@PathVariable LobbyType desiredLobby) {
        return chessLobbyService.getAllDesiredLobbies(desiredLobby);
    }

    @PostMapping("/api/joinLobby")
    @ResponseBody
    public String joinLobby(@RequestBody JoinLobbyRequest request) {
        Chess_Lobby lobby = chessLobbyService.getLobbyFromId(request.getLobbyId());
        if(chessLobbyService.isLobbyFull(lobby))
        {
            return "lobbyFull";
        }
        chessLobbyService.joinLobby(request);
        return "play="+request.getLobbyId();
    }

    @GetMapping("/api/getLobbyDTO")
    public LobbyDTO getLobbyDTO(@RequestParam String lobbyId, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        return chessLobbyService.getLobbyDTO(lobbyId);
    }
}
