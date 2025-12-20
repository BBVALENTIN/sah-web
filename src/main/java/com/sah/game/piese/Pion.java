package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.Tip;

public class Pion extends Piese {
    public Pion(int color, int row, int col) {
        super(color, row, col);
        tip = Tip.PION;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol)
    {
        int directie;
        if(color == 1)
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
        System.out.println("lovestePiese:"+lovestePiese + " this.miscata:"+miscata+" piesaInfata" + this.piesaInFata(targetRow, targetCol));
        return false;
    }
}
