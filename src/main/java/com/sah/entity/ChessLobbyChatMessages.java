package com.sah.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChessLobbyChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime sendDate;

    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(name="FK_MESSAGES_USERS"))
    private Users sender;

    @ManyToOne
    @JoinColumn(name ="chat_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MESSAGES_CHATS"))
    private ChessLobbyChats chat;
}
