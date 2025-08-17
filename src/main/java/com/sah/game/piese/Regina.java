package com.sah.game.piese;

public class Regina extends Piese{
    public Regina(int color, int row, int col) {
        super(color, row, col);
        this.tip = "regina";
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
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
