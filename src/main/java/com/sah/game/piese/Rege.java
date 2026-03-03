package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.Tip;


public class Rege extends Piese {
    public Rege(int color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        tip = Tip.REGE;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        Piese board[][] = game.getBoard();

        if(patratValid(targetRow, targetCol) && (Math.abs(targetRow - this.row) + Math.abs(targetCol - this.col) == 1 || Math.abs(targetRow - this.row) * Math.abs(targetCol - this.col) == 1)) {
            return true;
        }
       if(miscata == false)
       {
           // rocada mica
           if(targetCol == col + 2 && targetRow == row && piesaInFata(targetRow, targetCol) == false)
           {
               if(board[prerow][precol+3].miscata == false){
                   game.rocada = board[prerow][precol+3];
                   return true;
               }
           }

           // rocada mare
           if(targetCol == col - 2 && targetRow == row && !piesaInFata(targetRow, targetCol))
           {
               if(board[prerow][precol-4].miscata == false){
                   game.rocada = board[prerow][precol-4];
                   return true;
               }
           }
       }
        return false;
    }
}
