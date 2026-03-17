package com.sah.service;

import com.sah.dto.ChatMessageDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.MessageType;
import com.sah.repository.ChessLobbyChatRepository;
import com.sah.repository.LobbyChatMessagesService;
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
    @Autowired
    private LobbyChatMessagesService lobbyChatMessagesService;


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

    public ChessLobbyChatMessages sendMessage(String lobbyId,
                                              String sender,
                                              String content) {

        ChessLobbies lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow();

        ChessLobbyChats chat = lobby.getChat();

        Users user = userRepository.findByUsername(sender);

        ChessLobbyChatMessages message = new ChessLobbyChatMessages();

        message.setSenderId(user.getId());
        message.setSenderName(sender);
        message.setContent(content);
        message.setSendDate(LocalDateTime.now());
        message.setChat(chat);

        return lobbyChatMessagesService.save(message);
    }

    public ChatMessageDTO addUser(String sender, String lobbyId) {
        ChessLobbyChatMessages message = new ChessLobbyChatMessages();
        ChessLobbies lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow();
        Users user = userRepository.findByUsername(sender);
        ChessLobbyChats chat = lobby.getChat();

        message.setSenderId(user.getId());
        message.setSenderName(sender);
        message.setContent(MessageType.JOIN.toString());
        message.setSendDate(LocalDateTime.now());
        message.setChat(chat);

        lobbyChatMessagesService.save(message);
        return new ChatMessageDTO(sender, "", MessageType.JOIN);
    }
}
