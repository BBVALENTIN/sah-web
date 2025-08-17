package com.sah.game.piese;

import com.sah.game.ChessBoard;

public class Tura extends Piese {
    public Tura(int color, int row, int col){
        super(color, row, col);
        this.tip = "TURA";
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        System.out.println("DEBUG miscare: targetRow=" + targetRow + " targetCol=" + targetCol);

        if (targetRow == this.row || targetCol == this.col) {
            if (!piesaInFata(targetRow, targetCol) && patratValid(targetRow, targetCol)) {
                return true;
            }
        }

        System.out.println(
                "acelasiPatrat=" + acelasiPatrat(targetRow, targetCol) +
                        " lovestePiese=" + lovestePiese +
                        " this.miscata=" + miscata +
                        " piesaInFata=" + this.piesaInFata(targetRow, targetCol)
        );

        return false;
    }
}
