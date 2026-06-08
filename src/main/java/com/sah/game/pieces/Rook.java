package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Rook extends Pieces {
    public Rook(ColorType color, int row, int col, ChessBoard game){
        super(color, row, col, game);
        tip = Type.ROOK;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        if(acelasiPatrat(targetRow, targetCol)){
            return false;
        }

        if (targetRow == this.row || targetCol == this.col) {
            if (!piesaInFata(targetRow, targetCol) && patratValid(targetRow, targetCol)) {
                return true;
            }
        }


        return false;
    }

    @Override
    public boolean poateAjunge(int targetRow, int targetCol) {
        if ((targetRow == this.row || targetCol == this.col) && !piesaInFata(targetRow, targetCol)) {
            return true;
        }
        return false;
    }
}
