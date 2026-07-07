package com.sah.game;

import com.sah.game.GameEnums.ColorType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class OMoveResult {
    public MoveCoords lastMoveCoords;
    public String pgn;
    public String fen; // maybe get check and checkmate from fen string
    public List<OCapturedPiece> capturedPieceList;
    public boolean isCheck, isCheckMate;
    public ColorType currentColor;
}


/*
*  lastMoveCoords --> the move needed, can also highlight it if its correct
*  FEN --> current table status, check/checkmate, possible en passant
*  PGN --> if it is captures (contains x)
*  capturedPieces --> to showcase captured Pieces
* */