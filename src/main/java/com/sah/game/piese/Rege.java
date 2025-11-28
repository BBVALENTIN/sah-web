package com.sah.game.piese;

import com.sah.game.ChessBoard;

import static com.sah.game.ChessBoard.board;

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
//       if(miscata == false)
//       {
//           if(targetCol == col + 2 && targetRow == row && piesaInFata(targetRow, targetCol) == false)
//           {
//               if((this.color == 1 && board[7][7] != null) || (this.color == -1 && board[7][0] != null))
//                   Piese rocada =
//           }
//       }
        return false;
    }
}
