package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.Tip;

import static com.sah.game.ChessBoard.board;

public class Rege extends Piese {
    public Rege(int color, int row, int col) {
        super(color, row, col);
        tip = Tip.REGE;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        if(patratValid(targetRow, targetCol) && (Math.abs(targetRow - this.row) + Math.abs(targetCol - this.col) == 1 || Math.abs(targetRow - this.row) * Math.abs(targetCol - this.col) == 1)) {
            return true;
        }
       if(miscata == false)
       {
           if(targetCol == col + 2 && targetRow == row && piesaInFata(targetRow, targetCol) == false)
           {
               if(board[prerow][precol+3].miscata == false){
                   ChessBoard.rocada = board[prerow][precol+3];
//                   System.out.println("--------------DEBUG TURA NU O VEDE BINE "+board[prerow][precol+3] + "-----"+ ChessBoard.rocada);
//                   System.out.println("-----------------PREROW SI PRECOL SUNT" + prerow+ "-------------------"+ precol+3);
                   return true;
               }
           }
       }
        return false;
    }
}
