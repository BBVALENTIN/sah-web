package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Bishop extends Pieces {
    public Bishop(ColorType color, int row, int col, ChessBoard game)
    {
        super(color, row, col, game);
        tip = Type.BISHOP;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {

        if(patratValid(targetRow, targetCol) && Math.abs(targetRow - this.row) == Math.abs(targetCol - this.col) && piesaPeDiagonala(targetRow, targetCol) == false && !acelasiPatrat(targetRow, targetCol))
            return true;
        return false;
    }
}
