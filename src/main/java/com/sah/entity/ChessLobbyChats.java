package com.sah.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;

@Entity
@Getter
@Setter
public class ChessLobbyChats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @Column(nullable = false)
    private Long senderId;
    @Column(nullable = false)
    private Long content;
    @Column(nullable = false)
    private DateFormat sendDate;
}
