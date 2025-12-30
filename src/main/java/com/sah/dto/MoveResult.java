package com.sah.dto;

import java.util.List;
import com.sah.game.piese.Piese;

public class MoveResult {
    private boolean success;
    private String message;
    private List<PiesaDTO> updatedPieces;
    private boolean isCheck;
    private boolean isCheckmate;
    private int culoareCurenta;
    private String pgn;

    public MoveResult(boolean success, String message, List<PiesaDTO> updatedPieces, boolean isCheck, boolean isCheckmate, int culoareCurenta, String pgn) {
        this.success = success;
        this.message = message;
        this.updatedPieces = updatedPieces;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.culoareCurenta = culoareCurenta;
        this.pgn = pgn;
    }

    // Getters și Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}