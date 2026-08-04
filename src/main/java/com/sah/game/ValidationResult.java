package com.sah.game;

public record ValidationResult(Move move,
                               boolean isCapture,
                               boolean isCastling,
                               boolean isEnPassant,
                               boolean isPromotion) {
}
