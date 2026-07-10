package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class Queen extends Pieces {
    public Queen(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        type = Type.QUEEN;
    }

    @Override
    public boolean move(int targetRow, int targetCol) {
        if(sameSquare(targetRow, targetCol)){
            return false;
        }
        if(validSquare(targetRow, targetCol) && !pieceOnDiagonal(targetRow, targetCol)){
            if(Math.abs(targetRow - row) == Math.abs(targetCol - col)){
                return true;
            }
        }
        if(validSquare(targetRow, targetCol) && !pieceInFront(targetRow, targetCol)){
            if(targetRow == row || targetCol == col){
                return true;
            }
        }
        return false;
    }
}
