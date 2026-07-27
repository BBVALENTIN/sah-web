package com.sah.game;

import com.sah.dto.chess.*;
import com.sah.enums.Sides;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.gameenums.ColorType;
import com.sah.game.dtos.OCapturedPiece;
import com.sah.game.gameenums.ErrorCodes;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Getter
public class Game {
    private Board board;
    public short movesPlayed, halfMove;
    public boolean promoted, isCheck, isCheckMate, resignation, canCastle;
    public CastlingInfoDTO castlingInfo;
    private MoveNotation moveNotation = new MoveNotation();
    private List<OCapturedPiece> OCapturedPieces = new ArrayList<>();

    private ColorType currentColor;


    public Game() {
        this.board = new Board();
        this.currentColor = ColorType.WHITE;
    }

    public OMoveResult makeMove(MoveCoords moveCoords) throws InvalidMoveException {

        int fromRow = moveCoords.getFromRow();
        int fromCol = moveCoords.getFromCol();
        int targetRow = moveCoords.getTargetRow();
        int targetCol = moveCoords.getTargetCol();


        Move move = new Move(
                fromRow,
                fromCol,
                targetRow,
                targetCol,
                null,
                board.at(moveCoords.getFromRow(), moveCoords.getFromCol()).color,
                false
        );

        if(!board.canMove(fromRow, fromCol, targetRow, targetCol))
            throw new InvalidMoveException(ErrorCodes.ILLEGAL_MOVE);

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
}
