package com.sah.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChessLobbyChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    @Column(nullable = false)
    private Long senderId;
    @Column
    private String senderName;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime sendDate;

    @ManyToOne
    private ChessLobbyChats chat;
}
