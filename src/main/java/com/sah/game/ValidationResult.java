package com.sah.game;

public record ValidationResult(Move move, boolean isCapture, boolean isEnPassant, boolean isCastling) {
}
