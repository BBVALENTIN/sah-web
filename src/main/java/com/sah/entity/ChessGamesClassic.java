package com.sah.entity;

import com.sah.enums.ResultType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChessGamesClassic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ClassicGameId;
    @Column(nullable = false)
    private ResultType result;
    @Column(nullable = false)
    private int numberOfMoves;
    @Column(nullable = false, length = 2000)
    private String PGN;
    @Column(nullable = false)
    private String lobbyId;
    @Column(nullable = false)
    private boolean resignation;

}
