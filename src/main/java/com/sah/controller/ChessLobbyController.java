package com.sah.controller;

import com.sah.dto.ChatMessage;
import com.sah.service.ChessLobbyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ChessLobbyController {

    private final ChessLobbyService chessLobbyService;

    public ChessLobbyController(ChessLobbyService chessLobbyService) {
        this.chessLobbyService = chessLobbyService;
    }

    @PostMapping("/play/create")
    public String createLobby() {
        String lobby_Id = chessLobbyService.GenerateRandomLobbyId();
        return "redirect:/play="+lobby_Id;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setSender(principal.getName());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // add username in websocket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @GetMapping("/play={lobby_Id}")
    public String showLobby(@PathVariable String lobby_Id) {
        return "game/play";
    }
}
