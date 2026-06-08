package com.sah.dto.responses;

import java.util.List;

import com.sah.dto.chess.LastMove;
import com.sah.dto.chess.PieceDTO;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveResultDTO {
    private List<PieceDTO> updatedPieces;
    private boolean isCheck, isCheckmate;
    private ErrorCodes errorCodes;
    private ColorType currentColor;
    private String pgn;
    private boolean captures;
    private LastMove lastMove;
    private String FEN;
    private List<PieceDTO> capturedPieces;

    public MoveResultDTO(List<PieceDTO> updatedPieces, boolean isCheck, boolean isCheckmate, ColorType currentColor, String pgn, boolean captures, LastMove lastMove, String FEN, List<PieceDTO> capturedPieces) {
        this.updatedPieces = updatedPieces;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.currentColor = currentColor;
        this.pgn = pgn;
        this.captures = captures;
        this.lastMove = lastMove;
        this.FEN = FEN;
        this.capturedPieces = capturedPieces;
    }

    public MoveResultDTO(ErrorCodes errorCodes){
        this.errorCodes = errorCodes;
    }
}