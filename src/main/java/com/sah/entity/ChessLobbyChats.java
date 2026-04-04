package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChessLobbyChats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;


    @OneToMany(mappedBy = "chat",  cascade = CascadeType.ALL)
    private List<ChessLobbyChatMessages> messages = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "lobby_id", nullable = false, unique = true)
    private ChessLobbies lobby;

    @Column(nullable = false)
    private boolean reported;
}
