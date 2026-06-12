package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Bishop extends Pieces {
    public Bishop(ColorType color, int row, int col, ChessBoard game)
    {
        super(color, row, col, game);
        type = Type.BISHOP;
    }

    @Override
    public boolean move(int targetRow, int targetCol) {

        if(validSquare(targetRow, targetCol) && Math.abs(targetRow - this.row) == Math.abs(targetCol - this.col) && pieceOnDiagonal(targetRow, targetCol) == false && !sameSquare(targetRow, targetCol))
            return true;
        return false;
    }
}
