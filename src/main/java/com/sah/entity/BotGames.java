package com.sah.entity;

import com.sah.enums.ResultType;
import com.sah.enums.WinType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BotGames {
    @Id
    private String gameId;

    @Column(nullable = false)
    private String playerUsername;

    @Column(nullable = false)
    private String botSide; // 'w' sau 'b' sau sides

    @Column(nullable = false)
    private int stockfishDepth;

    @Column(nullable = false)
    private ResultType result;

    @Column(nullable = false)
    private WinType winReason;

    @Column(nullable = false)
    private int numberOfMoves;

    @Column(nullable = false, length = 2000)
    private String PGN;

    @Column(nullable = false)
    private LocalDateTime playedAt = LocalDateTime.now();
}
