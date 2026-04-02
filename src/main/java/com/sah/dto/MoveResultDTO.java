package com.sah.dto;

import java.awt.*;
import java.util.List;

import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveResultDTO {
    private List<PiesaDTO> updatedPieces;
    private boolean isCheck, isCheckmate;
    private ErrorCodes errorCodes;
    private ColorType culoareCurenta;
    private String pgn;
    private boolean captures;
    private LastMove lastMove;
    private String FEN;
    private List<PiesaDTO> pieseCapturate;

    public MoveResultDTO(List<PiesaDTO> updatedPieces, boolean isCheck, boolean isCheckmate, ColorType culoareCurenta, String pgn, boolean captures, LastMove lastMove, String FEN, List<PiesaDTO> pieseCapturate) {
        this.updatedPieces = updatedPieces;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.culoareCurenta = culoareCurenta;
        this.pgn = pgn;
        this.captures = captures;
        this.lastMove = lastMove;
        this.FEN = FEN;
        this.pieseCapturate = pieseCapturate;
    }

    public MoveResultDTO(ErrorCodes errorCodes){
        this.errorCodes = errorCodes;
    }
}