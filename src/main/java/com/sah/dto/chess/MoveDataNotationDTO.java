package com.sah.dto.chess;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.CastlingNotation;
import com.sah.game.Piece;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class MoveDataNotationDTO {
    public Piece piece;
    public int fromRow, fromCol;
    public int targetRow, targetCol;
    public boolean isCheck, isCheckMate, promoted, isCapture;
    public ColorType currentColor;
    public CastlingNotation castlingNotation;
    public List<Piece> oldPieces;
}
