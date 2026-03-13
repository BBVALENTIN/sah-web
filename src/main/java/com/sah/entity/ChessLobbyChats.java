package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChessLobbyChats {
    @Id
    private String chatId;

    @Column(nullable = false)
    private Long senderId;
    @Column
    private String senderName;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime sendDate;
    @Column(nullable = false)
    private boolean reported;
}
