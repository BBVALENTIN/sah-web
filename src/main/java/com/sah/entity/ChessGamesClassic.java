package com.sah.entity;

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
    private String result;
    @Column(nullable = false)
    private int number_of_moves = 0;
    @Column(nullable = false, length = 2000)
    private String PGN;
    @Column(nullable = false)
    private String lobbyId;
    @Column(nullable = false)
    private boolean resignation;

}
