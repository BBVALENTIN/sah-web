package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Tip;

import java.awt.*;

public class Rege extends Piese {
    public Rege(ColorType color, int row, int col, ChessBoard game) {
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
               if(!checkRocadaMica(color, row, precol+1, precol+2))
                   return false;
               if(board[prerow][precol+3].miscata == false){
                   game.rocada = board[prerow][precol+3];
                   return true;
               }
           }

           // rocada mare
           if(targetCol == col - 2 && targetRow == row && !piesaInFata(targetRow, targetCol))
           {
               if(!checkRocadaMare(color, row, precol-1, precol-2)) {
                   return false;
               }
               if(board[prerow][precol-4].miscata == false){
                   game.rocada = board[prerow][precol-4];
                   return true;
               }
           }
       }
        return false;
    }

    private boolean checkRocadaMica(ColorType culoareCurenta, int targetRow, int square1, int square2) {
        for(Piese p : game.pieseList) {
            if(p.color == culoareCurenta)
                continue;
            if(p.miscare(targetRow, square1) || p.miscare(targetRow, square2))
                return false;
        }
        return true;
    }
    private boolean checkRocadaMare(ColorType culoareCurenta, int targetRow, int square1, int square2) {
        for(Piese p : game.pieseList) {
            if(p.color == culoareCurenta)
                continue;
            if(p.miscare(targetRow, square1) || p.miscare(targetRow, square2))
                return false;
        }
        return true;
    }
}
