package com.sah.game.piese;

import com.sah.game.Tip;

public class Cal extends Piese {
    public Cal(int color, int row, int col)
    {
        super(color, row, col);
        TIP = Tip.CAL;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol)
    {
        if(patratValid(targetRow, targetCol) && peTabla(targetRow, targetCol))
            if(Math.abs(targetRow-row) * Math.abs(targetCol-col) == 2)
                return true;
        return false;
    }
}
