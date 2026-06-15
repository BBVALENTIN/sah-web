package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Rook extends Pieces {
    public Rook(ColorType color, int row, int col, ChessBoard game){
        super(color, row, col, game);
        type = Type.ROOK;
    }

    @Override
    public boolean move(int targetRow, int targetCol) {
        if(sameSquare(targetRow, targetCol)){
            return false;
        }

        if (targetRow == this.row || targetCol == this.col) {
            if (!pieceInFront(targetRow, targetCol) && validSquare(targetRow, targetCol)) {
                if(row == 0)
                    setPossibleShortCastleFalse();
                if(row == 7)
                    setPossibleLongCastleFalse();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canGetTo(int targetRow, int targetCol) {
        if ((targetRow == this.row || targetCol == this.col) && !pieceInFront(targetRow, targetCol)) {
            return true;
        }
        return false;
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
}
