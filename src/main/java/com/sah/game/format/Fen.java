package com.sah.game.format;

import com.sah.game.Board;
import com.sah.game.CastlingRights;
import com.sah.game.Game;
import com.sah.game.Piece;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class Fen {
    public String formatMove(Board board, ColorType turn, CastlingRights castlingRights, int halfMove, int fullMove) {
        StringBuilder fen = new StringBuilder();

        for(int r = 0; r < 8; r++)
        {
            int emptySquares = 0;
            for(int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if (p == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }

                    char typeChar = getCharFromTip(p.type);
                    fen.append(p.color == ColorType.WHITE ? Character.toUpperCase(typeChar) : Character.toLowerCase(typeChar));
                }
            }
            if(emptySquares > 0) fen.append(emptySquares);
            if(r < 7) fen.append("/");
        }

        String t = (turn == ColorType.WHITE) ? "w" : "b";
        String enPassant = "-"; // for now;
        String castling = generateCastlingRightsString(castlingRights);

        String returnedFEN = String.format("%s %s %s %s %d %d",
                fen.toString(), t, castling, enPassant, halfMove, fullMove);

        return returnedFEN;
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

    private String generateCastlingRightsString(CastlingRights rights)
    {
        StringBuilder sb = new StringBuilder();
        if(rights.whiteKingSide())
            sb.append('K');
        if(rights.whiteQueenSide())
            sb.append('Q');
        if(rights.blackKingSide())
            sb.append('k');
        if(rights.blackQueenSide())
            sb.append('q');

        return sb.toString();
    }
}

