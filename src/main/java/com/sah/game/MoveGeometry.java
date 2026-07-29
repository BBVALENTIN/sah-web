package com.sah.game;

import com.sah.game.dtos.MoveCoords;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public final class MoveGeometry {
    private MoveGeometry() {}

    public static boolean isValid(Type type, ColorType color, MoveCoords moveCoords)
    {
        int fromRow = moveCoords.getFromRow(), fromCol = moveCoords.getFromCol(), targetRow = moveCoords.getTargetRow(), targetCol = moveCoords.getTargetCol();
        int dR = Math.abs(fromRow - targetRow);
        int dC = Math.abs(fromCol - targetCol);

        return switch (type){
            case KING -> (dR + dC == 1) || (dR * dC == 1);
            case KNIGHT -> dR * dC == 2;
            case ROOK -> dR == 0 || dC == 0;
            case BISHOP -> dR == dC;
            case QUEEN -> dR == dC || (dR == 0 || dC == 0);
            case PAWN -> isValidPawnMove(color, moveCoords);
        };
    }

    private static boolean isValidPawnMove(ColorType color, MoveCoords moveCoords)
    {
        int direction = (color == ColorType.WHITE) ? -1 : 1;
        int dC = Math.abs(moveCoords.getTargetCol() - moveCoords.getFromCol());
        int dR = moveCoords.getTargetRow() - moveCoords.getFromRow();

        if(dC == 0 && dR == direction) return true;

        int startRow = (color == ColorType.WHITE) ? 6 : 1;
        if(dC == 0 && dR == direction * 2 && moveCoords.getFromRow() == startRow) return true;

        if(dC == 1 && dR == direction) return true;

        return false;
    }
}
