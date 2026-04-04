package com.sah.controller;

import com.sah.dto.ChatMessageDTO;
import com.sah.dto.CreateLobbyRequest;
import com.sah.dto.LobbyDTO;
import com.sah.entity.ChessLobbies;
import com.sah.service.ChessLobbyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ChessController {

    private final ChessLobbyService chessLobbyService;

    public ChessController(ChessLobbyService chessLobbyService) {
        this.chessLobbyService = chessLobbyService;
    }

    @PostMapping("/api/play/createQuick")
    @ResponseBody
    public LobbyDTO createQuickLobby(@RequestBody String username) {
        LobbyDTO newLobby = chessLobbyService.createLobby(username);
        return newLobby;
    }

//    @MessageMapping("/lobby.addAvailableLobby")
//    @SendTo("/topic/public")
    @PostMapping("/api/play/create")
    @ResponseBody
    public LobbyDTO createLobby(@RequestBody CreateLobbyRequest request) { // , @Payload lobbyDTO lobbyDTO, SimpMessageHeaderAccessor headerAccessor
//        headerAccessor.getSessionAttributes().put("lobbyId", lobbyDTO.getLobbyId());
        return chessLobbyService.createLobby(request.getUsername());
    }


    @GetMapping("/play={lobbyId}")
    public String showLobby(@PathVariable String lobbyId, HttpSession session, Principal principal) {
        chessLobbyService.registerPlayer(session.getId(), lobbyId);
        return "game/play";
    }
}