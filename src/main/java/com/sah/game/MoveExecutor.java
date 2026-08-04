package com.sah.game;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;
import lombok.AllArgsConstructor;

/*
* Check if the move puts the other king in danger
*
* */
@AllArgsConstructor
public class MoveExecutor {

    private final Situation situation;

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
        if(validation.isPromotion()) {
            board.setSquare(move.targetRow(), move.targetCol(), Piece.of(move.pieceColor(), move.promotion().toType()));
        }
        return board;
    }
}
