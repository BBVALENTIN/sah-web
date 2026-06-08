package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Queen extends Pieces {
    public Queen(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        tip = Type.QUEEN;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        if(acelasiPatrat(targetRow, targetCol)){
            return false;
        }
        if(patratValid(targetRow, targetCol) && !piesaPeDiagonala(targetRow, targetCol)){
            if(Math.abs(targetRow - row) == Math.abs(targetCol - col)){
                return true;
            }
        }
        if(patratValid(targetRow, targetCol) && !piesaInFata(targetRow, targetCol)){
            if(targetRow == row || targetCol == col){
                return true;
            }
        }
        return false;
    }
}
