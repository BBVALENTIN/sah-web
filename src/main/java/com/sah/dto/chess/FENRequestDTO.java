package com.sah.dto.chess;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.pieces.Pieces;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FENRequestDTO {
    public Pieces board[][];
    private ColorType currentColor;
    private short halfMove;
    private CastlingInfoDTO castlingInfo;
}
