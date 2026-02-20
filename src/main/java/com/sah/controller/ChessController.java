package com.sah.controller;

import com.sah.dto.ChatMessageDTO;
import com.sah.entity.Chess_Lobby;
import com.sah.enums.LobbyType;
import com.sah.service.ChessLobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sah.dto.lobbyDTO;

import java.security.Principal;

@Controller
public class ChessController {

    private final ChessLobbyService chessLobbyService;

    public ChessController(ChessLobbyService chessLobbyService) {
        this.chessLobbyService = chessLobbyService;
    }

    @PostMapping("/api/play/createQuick")
    @ResponseBody
    public Chess_Lobby createQuickLobby() {
        Chess_Lobby newLobby = chessLobbyService.createLobby(LobbyType.AVAILABLE);
        return newLobby;
    }

//    @MessageMapping("/lobby.addAvailableLobby")
//    @SendTo("/topic/public")
    @PostMapping("/api/play/create")
    @ResponseBody
    public Chess_Lobby createLobby(@RequestBody LobbyType Type) { // , @Payload lobbyDTO lobbyDTO, SimpMessageHeaderAccessor headerAccessor
//        headerAccessor.getSessionAttributes().put("lobbyId", lobbyDTO.getLobbyId());
        return chessLobbyService.createLobby(Type);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) {
        chatMessageDTO.setSender(principal.getName());
        return chatMessageDTO;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        // add username in websocket session
        headerAccessor.getSessionAttributes().put("username", chatMessageDTO.getSender());
        return chatMessageDTO;
    }


    @GetMapping("/play={lobby_Id}")
    public String showLobby(@PathVariable String lobby_Id) {
        return "game/play";
    }
}