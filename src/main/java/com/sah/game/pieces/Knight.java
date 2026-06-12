package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Knight extends Pieces {
    public Knight(ColorType color, int row, int col, ChessBoard game)
    {
        super(color, row, col, game);
        type = Type.KNIGHT;
    }

    @Override
    public boolean move(int targetRow, int targetCol)
    {
        if(validSquare(targetRow, targetCol) && peTabla(targetRow, targetCol))
            if(Math.abs(targetRow-row) * Math.abs(targetCol-col) == 2)
                return true;
        return false;
    }

    @Override
    public boolean canGetTo(int targetRow, int targetCol)
    {
        if(Math.abs(targetRow-row) * Math.abs(targetCol-col) == 2)
            return true;
        return false;
    }
}
