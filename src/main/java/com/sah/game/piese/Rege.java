package com.sah.game.piese;

public class Rege extends Piese {
    public Rege(int color, int row, int col) {
        super(color, row, col);
        this.tip = "rege";
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        if(patratValid(targetRow, targetCol) && (Math.abs(targetRow - this.row) + Math.abs(targetCol - this.col) == 1 || Math.abs(targetRow - this.row) * Math.abs(targetCol - this.col) == 1)) {
            return true;
        }
        return false;
    }
}
