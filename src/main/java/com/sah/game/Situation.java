package com.sah.game;

import com.sah.game.gameenums.ColorType;

import java.util.List;


public class Situation {
    public boolean isInCheck(Board board, ColorType kingColor) {}
    public boolean isCheckmate(Board board, ColorType kingColor) {}
    public boolean isStalemate(Board board, ColorType kingColor) {}
    public boolean isDrawBy50Moves(int halfMove) {
        return halfMove == 50;
    }
    public boolean isDrawByRepetition(List<Long> positionHashes) {}
}
