package com.sah.entity;

import com.sah.enums.FormatType;
import com.sah.enums.LobbyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChessLobbies {
    @Id
    private String lobbyId;

    @Column(nullable = false)
    private FormatType format;
    // TO CHANGE FROM NULLABLE = TRUE TO NULLABLE = FALSE WHEN DONE
    @Column(nullable = true)
    private String playerWhite;
    @Column(nullable = true)
    private String playerBlack;
    @Column(nullable = false)
    private LobbyType lobbyType;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "lobby", cascade = CascadeType.ALL)
    private ChessLobbyChats chat;

    @OneToOne(mappedBy = "lobby", cascade = CascadeType.REMOVE)
    private ChessGames chessGames;
}
