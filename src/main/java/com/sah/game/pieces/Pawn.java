package com.sah.game.pieces;

import com.sah.dto.chess.LastMove;
import com.sah.game.ChessBoard;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class Pawn extends Pieces {
    public Pawn(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        type = Type.PAWN;
    }

    @Override
    public boolean move(int targetRow, int targetCol)
    {
        int direction;
        LastMove lastMove = game.getLastMove();
        if(color == ColorType.WHITE)
            direction = -1;
        else
            direction = 1;
        this.hittedPiece = getHittedPiece(targetRow, targetCol);
        if(targetCol == col && targetRow == row+direction && this.hittedPiece == null)
            return true;
        if(targetCol == col && targetRow == row+direction*2 && this.moved == false && pieceInFront(targetRow, targetCol) == false && this.hittedPiece == null)
            return true;
        if(Math.abs(targetCol-col) == 1 && targetRow == row+direction && hittedPiece != null && hittedPiece.color != this.color)
            return true;
        return false;
    }
}
