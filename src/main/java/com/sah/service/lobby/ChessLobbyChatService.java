package com.sah.service.lobby;

import com.sah.dto.misc.ChatMessageDTO;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.MessageType;
import com.sah.repository.LobbyChatMessagesRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ChessLobbyChatService {

    private UserRepository userRepository;
    private LobbyRepository lobbyRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    private LobbyChatMessagesRepository lobbyChatMessagesService;


    public ChessLobbyChatMessages sendMessage(ChessLobbyChats chat,
                                              Users sender,
                                              String content) {

        ChessLobbyChatMessages message = ChessLobbyChatMessages.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .sendDate(LocalDateTime.now())
                .build();

        return lobbyChatMessagesService.save(message);
    }

    public ChatMessageDTO addUser(Users sender, ChessLobbyChats chat) {
        ChessLobbyChatMessages message = ChessLobbyChatMessages.builder()
                        .chat(chat).sender(sender).content(MessageType.JOIN.toString()).sendDate(LocalDateTime.now()).build();
        lobbyChatMessagesService.save(message);
        return new ChatMessageDTO(sender.getUsername(), "", MessageType.JOIN);
    }
}
