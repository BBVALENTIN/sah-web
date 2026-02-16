package com.sah.dto;

import java.util.List;

import com.sah.game.ErrorCodes;

public class MoveResultDTO {
    private List<PiesaDTO> updatedPieces;
    private boolean isCheck, isCheckmate;
    private ErrorCodes errorCodes;
    private int culoareCurenta;
    private String pgn;
    private boolean captures;

    public MoveResultDTO(List<PiesaDTO> updatedPieces, boolean isCheck, boolean isCheckmate, int culoareCurenta, String pgn, boolean captures) {
        this.updatedPieces = updatedPieces;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.culoareCurenta = culoareCurenta;
        this.pgn = pgn;
        this.captures = captures;
    }

    public MoveResultDTO(ErrorCodes errorCodes){
        this.errorCodes = errorCodes;
    }

    // Getters și Setters

    public ErrorCodes getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(ErrorCodes errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<PiesaDTO> getUpdatedPieces() {
        return updatedPieces;
    }

    public void setUpdatedPieces(List<PiesaDTO> updatedPieces) {
        this.updatedPieces = updatedPieces;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public int getCuloareCurenta() {
        return culoareCurenta;
    }

    public void setCuloareCurenta(int culoareCurenta) {
        this.culoareCurenta = culoareCurenta;
    }

    public String getPgn() { return pgn; }

    public void setPgn(String pgn) { this.pgn = pgn; }

    public boolean isCaptures() { return captures;}
    public void setCaptures(boolean captures) { this.captures = captures; }
}