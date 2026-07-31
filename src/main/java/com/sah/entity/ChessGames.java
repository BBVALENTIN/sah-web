package com.sah.entity;

import com.sah.enums.ResultType;
import com.sah.enums.WinType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class ChessGames {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;
    @Column(nullable = false)
    private ResultType result;
    @Column(nullable = false)
    private int numberOfMoves;
    @Column(nullable = false, length = 2000)
    private String PGN;
    @Column(nullable = false)
    private WinType WinReason;

    @OneToOne
    @JoinColumn(name = "lobby_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_CHESS_GAMES_LOBBIES"))
    private ChessLobbies lobby;

}
