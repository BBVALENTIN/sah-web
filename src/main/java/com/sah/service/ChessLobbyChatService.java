package com.sah.service;

import com.sah.dto.ChatMessageDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.MessageType;
import com.sah.repository.ChessLobbyChatRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class ChessLobbyChatService {
    private String chatIdPossibleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ";
    private final short maxSize = 5;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private ChessLobbyChatRepository chessLobbyChatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    private SimpMessagingTemplate simpMessagingTemplate;

    private String generateChatId() {
        StringBuilder sb = new StringBuilder();
        sb.append("CH");
        for(int i = 0; i < maxSize; i++) {
            int index = random.nextInt(chatIdPossibleChars.length());
            sb.append(chatIdPossibleChars.charAt(index));
        }
        return sb.toString();
    }

    public String assignLobbyChatId() {
        String lobbyChatId;
        do {
            lobbyChatId = generateChatId();
        } while (chessLobbyChatRepository.findByChatId(lobbyChatId) != null);
        return lobbyChatId;
    }

    public ChatMessageDTO sendMessage(String username, String content, String lobbyId) {
        Users user = userRepository.findByUsername(username);
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);
        ChessLobbyChats message = new ChessLobbyChats(lobby.getChatId(), user.getId(), username, content, LocalDateTime.now(), false);

        chessLobbyChatRepository.save(message);
        return new ChatMessageDTO(content, username, MessageType.CHAT, lobby.getLobbyId());
    }
}
