package com.sah.game;

import com.sah.dto.chess.*;
import com.sah.enums.Sides;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.format.Fen;
import com.sah.game.format.Pgn;
import com.sah.game.gameenums.ColorType;
import com.sah.game.dtos.OCapturedPiece;
import com.sah.game.gameenums.ErrorCodes;
import com.sah.game.gameenums.Type;
import jakarta.persistence.GenerationType;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Game {
    private Board board;
    private List<OCapturedPiece> OCapturedPieces = new ArrayList<>();
    private int halfMove, fullMove;
    private boolean isCheck, isCheckMate;
    private CastlingRights castlingRights;

    private ColorType currentColor;
    private final MoveValidator validator;
    private final MoveExecutor executor;
    private final Situation situation;

    private final Fen fenFormatter;
    private final Pgn pgnFromatter;


    public Game() {
        this.board = new Board();
        this.currentColor = ColorType.WHITE;

        this.validator = new MoveValidator();
        this.situation = new Situation(validator);
        this.executor = new MoveExecutor(situation);
        this.fenFormatter = new Fen();
        this.pgnFromatter = new Pgn();
        this.castlingRights = CastlingRights.standard();
        this.isCheck = false;
        this.isCheckMate = false;
    }

    public OMoveResult makeMove(MoveCoords moveCoords) throws InvalidMoveException {

        int fromRow = moveCoords.getFromRow();
        int fromCol = moveCoords.getFromCol();
        int targetRow = moveCoords.getTargetRow();
        int targetCol = moveCoords.getTargetCol();

        if(board.isEmpty(fromRow, fromCol))
            throw new InvalidMoveException(ErrorCodes.UNDETECTED_PIECE);

        Move move = new Move(
                fromRow,
                fromCol,
                targetRow,
                targetCol,
                board.at(fromRow, fromCol).type,
                board.at(moveCoords.getFromRow(), moveCoords.getFromCol()).color
        );

        Board oldBoard = new Board(board);

        ValidationResult validationResult = validator.validate(board, move, castlingRights)
                .orElseThrow(() -> new InvalidMoveException(ErrorCodes.ILLEGAL_MOVE));

        if(validationResult.isCapture()) {
            OCapturedPieces.add(getOCapturedPiece(move.targetRow(), move.targetCol()));
            halfMove = 0;
        }

        if(move.pieceType() == Type.PAWN)
            halfMove = 0;
        fullMove++;

        executor.execute(this, move, validationResult);
        ColorType opponent = currentColor == ColorType.WHITE ? ColorType.BLACK : ColorType.WHITE;
        this.isCheck = situation.isInCheck(board, opponent);
        this.isCheckMate = this.isCheck && situation.easyIsCheckmate(this.isCheck ,board, opponent, castlingRights);
        currentColor = switchTurn(currentColor); // looks bad! IM BAD! IM REALLY REALLY BAD!


        // for now
        return OMoveResult.builder()
                .lastMoveCoords(new MoveCoords(move.fromRow(), move.fromCol(), move.targetRow(), move.targetCol()))
                .capturedPieceList(OCapturedPieces)
                .isCheck(this.isCheck)
                .isCheckMate(this.isCheckMate)
                .pgn(pgnFromatter.format(move, oldBoard, this.isCheck, this.isCheckMate, validationResult.isCastling()))
                .fen(fenFormatter.formatMove(board, currentColor, castlingRights, halfMove ,fullMove))
                .currentColor(currentColor)
                .build();

        /*
        * construct Move
        * getCapturedPieces();
        * checkPromotion();
        * check()
        * checkmate()
        * generatePGN();
        * generateFEN();
        *
        * */

    }

    private OCapturedPiece getOCapturedPiece(int tR, int tC)
    {
        Piece p = board.at(tR, tC);
        return new OCapturedPiece(p.type, p.color);
    }

    private ColorType switchTurn(ColorType currentColor){
        if(currentColor == ColorType.WHITE)
            currentColor = ColorType.BLACK;
        else
            currentColor = ColorType.WHITE;
        return currentColor;
    }
}
