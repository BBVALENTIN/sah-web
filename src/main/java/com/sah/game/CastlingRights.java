package com.sah.game;

import com.sah.game.gameenums.ColorType;

public record CastlingRights(
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide
) {
    public static CastlingRights standard()
    {
        return new CastlingRights(true, true, true, true);
    }

    public CastlingRights without(ColorType color){
        return switch (color) {
            case WHITE -> new CastlingRights(false, false, blackKingSide, blackQueenSide);
            case BLACK -> new CastlingRights(whiteKingSide, whiteQueenSide, false, false);
        };
    }
}
