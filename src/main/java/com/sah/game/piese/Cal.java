package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Tip;

public class Cal extends Piese {
    public Cal(ColorType color, int row, int col, ChessBoard game)
    {
        super(color, row, col, game);
        tip = Tip.KNIGHT;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol)
    {
        if(patratValid(targetRow, targetCol) && peTabla(targetRow, targetCol))
            if(Math.abs(targetRow-row) * Math.abs(targetCol-col) == 2)
                return true;
        return false;
    }

    @Override
    public boolean poateAjunge(int targetRow, int targetCol)
    {
        if(Math.abs(targetRow-row) * Math.abs(targetCol-col) == 2)
            return true;
        return false;
    }
}
