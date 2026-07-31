package com.sah.game;

import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.ErrorCodes;
import com.sah.game.gameenums.Type;

import java.util.Optional;

/*
* In case piece is king -- if the move is a queenside or kingside castle (or a castle)
* also if the move puts the king in danger
* In case it's a pawn, check if it's a valid promotion - later though
* In case it's another piece, check if the move puts the king in danger
*
* */
public class MoveValidator {

    private record kingCoords(int kingRow, int kingCol) {}

    public Optional<ValidationResult> validate(Board board, Move move, CastlingRights castlingRights) throws InvalidMoveException {

        Board copyBoard = new Board(board);
        Piece p = board.at(move.fromRow(), move.fromCol());
        boolean isCapture = false;
        boolean isCastling = false;
        boolean isEnPassant = false;
        boolean isPromotion = false;

        if(move.pieceType() == Type.KING && checkCastle(move.fromRow(), move.fromCol(), move.targetRow(), move.targetCol(), copyBoard)) {
            if (canCastle(board, move, p.color, castlingRights)) {
                isCastling = true;
                boolean kingSide = move.targetCol() > move.fromCol();
                if(kingSide == true)
                    copyBoard.executeKingCastle(p.color);
                else
                    copyBoard.executeQueenCastle(p.color);

                return Optional.of(new ValidationResult(move, isCapture, isCastling, isEnPassant, isPromotion));
            }
        }

        if(!copyBoard.canMove(move.fromRow(), move.fromCol(), move.targetRow(), move.targetCol()))
            throw new InvalidMoveException(ErrorCodes.ILLEGAL_MOVE);

        if(!board.isEmpty(move.targetRow(), move.targetCol()))
            isCapture = true;

        if(p.type == Type.PAWN && isPromotion(move.targetRow(), p.color)) {
            if(move.promotion() == null)
                return Optional.empty();
            isPromotion = true;
        } else if (move.promotion() != null) {
            return Optional.empty();
        }

        copyBoard.movePiece(move.fromRow(), move.fromCol(), move.targetRow(), move.targetCol());

        if(isMyKingInCheck(copyBoard, move.pieceColor()))
            throw new InvalidMoveException(ErrorCodes.KING_IN_CHECK);

        return Optional.of(new ValidationResult(move, isCapture, isCastling, isEnPassant, isPromotion));
    }

    private boolean checkCastle(int fR, int fC, int tR, int tC, Board board) {
        // add the possibility that a opponent piece may attack the castling squares
        // check if the rooks are still there
        if(tC - fC == 2 && (board.isEmpty(fR, tC-1) && board.isEmpty(fR, fC+1)))
            return true;

        if(tC - fC == -2 && (board.isEmpty(fR, tC + 1) && board.isEmpty(fR, fC - 1) && board.isEmpty(fR, tC -1)))
            return true;
        return false;
    }

    //check promotion
    private boolean isPromotion(int tR, ColorType color) {
        return (color == ColorType.WHITE && tR == 0) || (color == ColorType.BLACK && tR == 7);
    }

    private kingCoords getKing(Board board, ColorType color) {
        // implement check based on the king color, if black - search from 0 to 7, if white from 7 to 0
        Piece king = null;
        int kingRow = -1, kingCol = -1;
        outerloop:
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++){
                if (!board.isEmpty(r, c) && board.at(r, c).type == Type.KING && board.at(r, c).color == color) {
                    kingRow = r;
                    kingCol = c;
                    break outerloop;
                }
                }
        return new kingCoords(kingRow, kingCol);
    }

    private boolean isMyKingInCheck(Board board, ColorType color)
    {
        kingCoords king = getKing(board, color);
        for(int r = 0; r < 8; r++)
            for(int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if(p != null && p.color != color)
                    if(board.canMove(r, c, king.kingRow, king.kingCol))
                        return true;
            }
        return false;
    }

    private boolean canCastle(Board board, Move move, ColorType color, CastlingRights castlingRights)
    {
        boolean kingSide = move.targetCol() > move.fromCol();

        if(color == ColorType.WHITE) {
            if(kingSide && castlingRights.whiteKingSide() == false) return false;
            if(kingSide == false && castlingRights.whiteQueenSide() == false) return false;
        } else {
            if(kingSide && castlingRights.blackKingSide() == false) return false;
            if(kingSide == false && castlingRights.blackQueenSide() == false) return false;
        }

        if(isMyKingInCheck(board, color)) return false;

        //check eventual attacks on rows
        return true;
    }

    private boolean hasLegalMoves(Board board, ColorType sideToMove, CastlingRights castlingRights) {
        for(int r = 0; r < 8; r++) {
            for(int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if(p == null || p.color != sideToMove) continue;

                for(int tr = 0; tr < 8; tr++)
                    for(int tc = 0; tc < 8; tc++) {
                        Move test = new Move(r, c, tr, tc, board.at(r, c).type, sideToMove, null);

                        if(p.type == Type.PAWN && (tr == 0 || tr == 7)) {
                            test = new Move(r, c, tr, tc, Type.QUEEN, sideToMove, null);
                        }

                        try {
                            Optional<ValidationResult> result = validate(board, test, castlingRights);
                            if(result.isPresent()) return true;
                        } catch(InvalidMoveException e) {
                        }
                    }
            }
        }
        return false;
    }

}
