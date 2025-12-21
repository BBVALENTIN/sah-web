package com.sah.game.piese;

import com.sah.game.Tip;

public class Nebun  extends Piese {
    public Nebun(int color, int row, int col)
    {
        super(color, row, col);
        tip = Tip.NEBUN;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {

        if(patratValid(targetRow, targetCol) && Math.abs(targetRow - this.row) == Math.abs(targetCol - this.col) && piesaPeDiagonala(targetRow, targetCol) == false && !acelasiPatrat(targetRow, targetCol))
            return true;
//        System.out.println("lovestePiese:"+lovestePiese+" piesaPeDiagonala" + this.piesaPeDiagonala(targetRow, targetCol) + " patratvalid: "+ patratValid(targetRow, targetCol));
        return false;
    }
}
