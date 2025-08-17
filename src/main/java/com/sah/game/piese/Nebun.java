package com.sah.game.piese;

public class Nebun  extends Piese {
    public Nebun(int color, int row, int col)
    {
        super(color, row, col);
        this.tip = "nebun";
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        if(patratValid(targetRow, targetCol) && Math.abs(targetRow - this.row) == Math.abs(targetCol - this.col) && piesaPeDiagonala(targetRow, targetCol) == false)
            return true;
        System.out.println("lovestePiese:"+lovestePiese+" piesaPeDiagonala" + this.piesaPeDiagonala(targetRow, targetCol) + " patratvalid: "+ patratValid(targetRow, targetCol));
        return false;
    }
}
