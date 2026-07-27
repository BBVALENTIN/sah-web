package com.sah.game;

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
}
