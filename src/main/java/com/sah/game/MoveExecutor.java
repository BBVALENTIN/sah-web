package com.sah.game;

import com.sah.game.gameenums.Type;

/*
* Check if the move puts the other king in danger
*
* */
public class MoveExecutor {

    public Board execute(Game game, Move move, ValidationResult validation)
    {
        Board board = game.getBoard();
        if(validation.isCastling() && move.targetCol() - move.fromCol() == 2) {
            board.executeKingCastle(move.pieceColor());
            game.setCastlingRights(game.getCastlingRights().without(move.pieceColor()));
            return board;
        }
        else if (validation.isCastling() && move.targetCol() - move.fromCol() == -2) {
            board.executeQueenCastle(move.pieceColor());
            game.setCastlingRights(game.getCastlingRights().without(move.pieceColor()));
            return board;
        }

        if(move.pieceType() == Type.KING)
            game.setCastlingRights(game.getCastlingRights().without(move.pieceColor()));

        board.movePiece(move.fromRow(), move.fromCol(), move.targetRow(), move.targetCol());
        return board;
    }
}
