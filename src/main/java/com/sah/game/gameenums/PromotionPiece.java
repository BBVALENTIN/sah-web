package com.sah.game.gameenums;

public enum PromotionPiece {
    q, r, b, n;

    public Type toType() {
        return switch (this) {
            case q -> Type.QUEEN;
            case r -> Type.ROOK;
            case b -> Type.BISHOP;
            case n -> Type.KNIGHT;
        };
    }
}
