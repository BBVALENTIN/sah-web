package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class King extends Pieces {
    public King(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        tip = Type.KING;
    }

    @Override
    public boolean miscare(int targetRow, int targetCol) {
        Pieces board[][] = game.getBoard();

        if(patratValid(targetRow, targetCol) && (Math.abs(targetRow - this.row) + Math.abs(targetCol - this.col) == 1 || Math.abs(targetRow - this.row) * Math.abs(targetCol - this.col) == 1)) {
            return true;
        }
       if(miscata == false)
       {
           if(!checkRocada(color, row, precol))
               return false;

           if(targetCol == col + 2 && targetRow == row && piesaInFata(targetRow, targetCol) == false)
           {
               if(!checkRocadaMica(color, row, precol+1, precol+2))
                   return false;
               if(board[prerow][precol+3].miscata == false){
                   game.castling = board[prerow][precol+3];
                   return true;
               }
           }

           if(targetCol == col - 2 && targetRow == row && !piesaInFata(targetRow, targetCol))
           {
               if(!checkRocadaMare(color, row, precol-1, precol-2)) {
                   return false;
               }
               if(board[prerow][precol-4].miscata == false){
                   game.castling = board[prerow][precol-4];
                   return true;
               }
           }
       }
        return false;
    }

    private boolean checkRocadaMica(ColorType culoareCurenta, int targetRow, int square1, int square2) {
        for(Pieces p : game.piecesList) {
            if(p.color == culoareCurenta) continue;
            if(p.tip == Type.KING) continue;
            if(p.miscare(targetRow, square1) || p.miscare(targetRow, square2)) {
                game.canCastle = true;
                return false;
            }
        }
        return true;
    }

    private boolean checkRocadaMare(ColorType culoareCurenta, int targetRow, int square1, int square2) {
        for(Pieces p : game.piecesList) {
            if(p.color == culoareCurenta) continue;
            if(p.tip == Type.KING) continue;
            if(p.miscare(targetRow, square1) || p.miscare(targetRow, square2)) {
                game.canCastle = true;
                return false;
            }
        }
        return true;
    }

    private boolean checkRocada(ColorType culoareCurenta, int kingRow,int kingCol) {
        for(Pieces p : game.piecesList) {
            if(p.color == culoareCurenta) continue;
            if(p.tip == Type.KING) continue;
            if(p.miscare(kingRow, kingCol)) {
                game.canCastle = false;
                return false;
            }
        }
        return true;
    }
}
