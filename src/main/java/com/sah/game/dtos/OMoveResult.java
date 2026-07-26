package com.sah.game.dtos;

import com.sah.game.gameenums.ColorType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OMoveResult {
    private MoveCoords lastMoveCoords;
    private String pgn;
    private String fen; // maybe get check and checkmate from fen string
    private List<OCapturedPiece> capturedPieceList;
    private boolean isCheck, isCheckMate;
    private ColorType currentColor;
}


/*
*  lastMoveCoords --> the move needed, can also highlight it if its correct
*  FEN --> current table status, check/checkmate, possible en passant
*  PGN --> if it is captures (contains x)
*  capturedPieces --> to showcase captured Piece
* */