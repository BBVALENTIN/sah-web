package com.sah.game.pieces;

import com.sah.dto.chess.LastMove;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Pawn extends Pieces {
    public Pawn(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        tip = Type.PAWN;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol)
    {
        int directie;
        LastMove lastMove = game.getLastMove();
        if(color == ColorType.WHITE)
            directie = -1;
        else
            directie = 1;
        this.lovestePiese = getLovesteP(targetRow, targetCol);
        if(targetCol == col && targetRow == row+directie && this.lovestePiese == null)
            return true;
        if(targetCol == col && targetRow == row+directie*2 && this.miscata == false && piesaInFata(targetRow, targetCol) == false && this.lovestePiese == null)
            return true;
        if(Math.abs(targetCol-col) == 1 && targetRow == row+directie && lovestePiese != null && lovestePiese.color != this.color)
            return true;
        return false;
    }
}
