package com.sah.game.dto;

import java.util.List;
import com.sah.game.piese.Piese;

public class MoveResult {
    private boolean success;
    private String message;
    private List<Piese> updatedPieces;
    private boolean isCheck;
    private boolean isCheckmate;
    private int currentPlayerColor;

    public MoveResult(boolean success, String message, List<Piese> updatedPieces) {
        this.success = success;
        this.message = message;
        this.updatedPieces = updatedPieces;
//        this.isCheck = isCheck;
//        this.isCheckmate = isCheckmate;
//        this.currentPlayerColor = currentPlayerColor;
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

    public List<Piese> getUpdatedPieces() {
        return updatedPieces;
    }

    public void setUpdatedPieces(List<Piese> updatedPieces) {
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

    public int getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public void setCurrentPlayerColor(int currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }
}