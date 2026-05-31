package com.sah.dto;

import com.sah.game.ChessBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotStartResponseDTO {
    private String gameId;
    private ChessBoard board;
}
