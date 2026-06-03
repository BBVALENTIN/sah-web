package com.sah.service;

import com.sah.dto.misc.ChatMessageDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.Users;
import com.sah.enums.MessageType;
import com.sah.repository.LobbyChatMessagesRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChessLobbyChatService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private LobbyChatMessagesRepository lobbyChatMessagesService;


    public ChessLobbyChatMessages sendMessage(String lobbyId,
                                              String sender,
                                              String content) {

        Users user = userRepository.findByUsername(sender);

        ChessLobbyChatMessages message = new ChessLobbyChatMessages();

        message.setSenderId(user.getUserId());
        message.setSenderName(sender);
        message.setContent(content);
        message.setSendDate(LocalDateTime.now());

        return lobbyChatMessagesService.save(message);
    }

    public ChatMessageDTO addUser(String sender, String lobbyId) {
        ChessLobbyChatMessages message = new ChessLobbyChatMessages();
        ChessLobbies lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow();
        Users user = userRepository.findByUsername(sender);

        message.setSenderId(user.getUserId());
        message.setSenderName(sender);
        message.setContent(MessageType.JOIN.toString());
        message.setSendDate(LocalDateTime.now());

        lobbyChatMessagesService.save(message);
        return new ChatMessageDTO(sender, "", MessageType.JOIN);
    }
}
