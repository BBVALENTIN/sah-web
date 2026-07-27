package com.sah.game.format;

import com.sah.game.Board;
import com.sah.game.Move;
import com.sah.game.Piece;
import com.sah.game.gameenums.Type;

public class Pgn {
    public String format(Move move, Board boardBefore, boolean isCheck, boolean isCheckMate)
    {
        if(move.isCastling()) {
            if(move.fromCol() < move.targetCol())
                return "O-O";
            else
                return "O-O-O";
        }

        char pieceChar = Character.toUpperCase(getCharFromTip(move.pieceType()));

        char colChar = (char)('a' + move.targetCol());
        int boardRow = 8 - move.targetRow();

        char fromColChar = (char)('a' + move.fromCol());
        int fromBoardRow = 8 - move.fromRow();

        String disambiguation = "";
        if(move.pieceType() != Type.PAWN) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece p = boardBefore.at(i, j);
                    if (p == null)
                        continue;
                    if ((p.color == move.pieceColor() && p.type == move.pieceType()) && boardBefore.canMove(i, j, move.targetRow(), move.targetCol())) {
                        if (j == move.fromCol()) {
                            disambiguation = "" + fromBoardRow;
                        } else {
                            disambiguation = "" + fromColChar;
                        }
                    }
                }
            }
        }

        String notation = "";

        if(move.pieceType() == Type.PAWN) {
            if(boardBefore.isEmpty(move.targetRow(), move.targetCol())) {
                notation = fromColChar + "x" + colChar+ boardRow;
            }
            else {
                notation = "" + colChar + boardRow;
            }
        }
        else {
            if(boardBefore.isEmpty(move.targetRow(), move.targetCol())) {
                notation = "" + pieceChar + disambiguation +"x" + colChar + boardRow;
            }
            else {
                notation = "" + pieceChar + disambiguation + colChar +boardRow;
            }
        }

        // implement promotion

        if(isCheck && !isCheckMate)
            notation = notation + "+";
        if(isCheckMate)
            notation = notation + "#";

        return notation;
    }

     private char getCharFromTip(Type tip) {
        return switch (tip) {
            case PAWN -> 'p';
            case KNIGHT -> 'n';
            case BISHOP -> 'b';
            case ROOK -> 'r';
            case QUEEN -> 'q';
            case KING -> 'k';
            default -> ' ';
        };
    }

}
