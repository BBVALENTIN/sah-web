package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.Tip;

public class Tura extends Piese {
    public Tura(int color, int row, int col){
        super(color, row, col);
        tip = Tip.TURA;
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
        if (targetRow == this.row || targetCol == this.col) {
            return true;
        }
        return false;
    }
}
