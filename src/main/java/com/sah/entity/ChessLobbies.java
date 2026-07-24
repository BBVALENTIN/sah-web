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

    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id", foreignKey = @ForeignKey(name="FK_USERS_LOBBY_PLAYERWHITE"))
    private Users playerWhite;
    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id", foreignKey = @ForeignKey(name="FK_USERS_LOBBY_PLAYERBLACK"))
    private Users playerBlack;
    @Column(nullable = false)
    private LobbyType lobbyType;
    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "lobby", cascade = CascadeType.ALL)
    private ChessLobbyChats chat;

    @OneToOne(mappedBy = "lobby", cascade = CascadeType.REMOVE)
    private ChessGames chessGames;
}
