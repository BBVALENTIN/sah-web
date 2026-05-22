package com.sah.controller;

import com.sah.dto.CreateLobbyRequest;
import com.sah.dto.JoinLobbyRequest;
import com.sah.dto.LobbyDTO;
import com.sah.entity.ChessLobbies;
import com.sah.enums.LobbyType;
import com.sah.repository.LobbyRepository;
import com.sah.service.ChessLobbyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PostMapping("/api/joinLobby/{lobbyId}")
    @ResponseBody
    public String joinLobby(@PathVariable String lobbyId, Principal principal) {
        ChessLobbies lobby = chessLobbyService.getLobbyFromId(lobbyId);
        if(chessLobbyService.isLobbyFull(lobby))
        {
            return "lobbyFull";
        }
        chessLobbyService.joinLobby(lobbyId, principal.getName());
        return "play="+lobbyId;
    }

    @GetMapping("/api/getLobbyDTO")
    public LobbyDTO getLobbyDTO(@RequestParam String lobbyId, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        return chessLobbyService.getLobbyDTO(lobbyId);
    }

    @PostMapping("/api/play/createQuick")
    @ResponseBody
    public LobbyDTO createQuickLobby(@RequestBody String username) {
        LobbyDTO newLobby = chessLobbyService.createLobby(username);
        return newLobby;
    }

    @PostMapping("/api/play/create")
    @ResponseBody
    public LobbyDTO createLobby(@RequestBody CreateLobbyRequest request) { // , @Payload lobbyDTO lobbyDTO, SimpMessageHeaderAccessor headerAccessor
        return chessLobbyService.createLobby(request.getUsername());
    }


    @GetMapping("/play={lobbyId}")
    public String showLobby(@PathVariable String lobbyId, HttpSession session, Principal principal) {
        chessLobbyService.registerPlayer(session.getId(), lobbyId);
        return "game/play";
    }
}
