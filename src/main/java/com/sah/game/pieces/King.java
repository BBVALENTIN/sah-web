package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class King extends Pieces {
    public King(ColorType color, int row, int col, ChessBoard game) {
        super(color, row, col, game);
        type = Type.KING;
    }

    @Override
    public boolean move(int targetRow, int targetCol) {
        Pieces board[][] = game.getBoard();

        if(validSquare(targetRow, targetCol) && (Math.abs(targetRow - this.row) + Math.abs(targetCol - this.col) == 1 || Math.abs(targetRow - this.row) * Math.abs(targetCol - this.col) == 1)) {
            return true;
        }

        if(moved == true) {
            setAllCastleFalse();
        }

        if(moved == false)
        {
           if(!checkCastle(color, row, precol))
               return false;

           if(targetCol == col + 2 && targetRow == row && pieceInFront(targetRow, targetCol) == false)
           {
               if(!checkSmallCastle(color, row, precol+1, precol+2))
                   return false;
               if(board[prerow][precol+3].moved == false) {
                   game.castling = board[prerow][precol + 3];
                   setAllCastleFalse();
                   return true;
               }
           }

           if(targetCol == col - 2 && targetRow == row && !pieceInFront(targetRow, targetCol))
           {
               if(!checkBigCastle(color, row, precol-1, precol-2)) {
                   return false;
               }
               if(board[prerow][precol-4].moved == false){
                   game.castling = board[prerow][precol-4];
                   setAllCastleFalse();
                   return true;
               }
           }
       }
        return false;
    }

    private boolean checkBigCastle(ColorType currentColor, int targetRow, int square1, int square2) {
        for(Pieces p : game.piecesList) {
            if(p.color == currentColor) continue;
            if(p.type == Type.KING) continue;
            if(p.move(targetRow, square1) || p.move(targetRow, square2)) {
                game.canCastle = false;
                return false;
            }
        }
        game.canCastle = true;
        return true;
    }

    private boolean checkSmallCastle(ColorType currentColor, int targetRow, int square1, int square2) {
        for(Pieces p : game.piecesList) {
            if(p.color == currentColor) continue;
            if(p.type == Type.KING) continue;
            if(p.move(targetRow, square1) || p.move(targetRow, square2)) {
                game.canCastle = false;
                return false;
            }
        }
        game.canCastle = true;
        return true;
    }

    private boolean checkCastle(ColorType currentColor, int kingRow, int kingCol) {
        for(Pieces p : game.piecesList) {
            if(p.color == currentColor) continue;
            if(p.type == Type.KING) continue;
            if(p.move(kingRow, kingCol)) {
                game.canCastle = false;
                return false;
            }
        }
        game.canCastle = true;
        return true;
    }

    private void setPossibleShortCastleFalse() {
        if(color == ColorType.WHITE)
            game.castlingInfo.possibleCastleWhiteShort = false;
        else game.castlingInfo.possibleCastleBlackShort = false;
    }

    private void setPossibleLongCastleFalse() {
        if(color == ColorType.WHITE)
            game.castlingInfo.possibleCastleWhiteLong = false;
        else game.castlingInfo.possibleCastleBlackLong = false;
    }

    private void setAllCastleFalse() {
        setPossibleLongCastleFalse();
        setPossibleShortCastleFalse();
    }
}
