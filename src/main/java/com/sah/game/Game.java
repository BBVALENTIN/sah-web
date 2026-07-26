package com.sah.game;

import com.sah.dto.chess.*;
import com.sah.enums.Sides;
import com.sah.game.gameenums.ColorType;
import com.sah.game.dtos.OCapturedPiece;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class Game {
    private Board board;
    private Piece castling;
    private Piece checkingPiece, selectedPiece, capturedPiece;
    public short movesPlayed, halfMove;
    public boolean promoted, isCheck, isCheckMate, resignation, canCastle;
    public CastlingInfoDTO castlingInfo;
    private LastMove lastMove;
    private MoveNotation moveNotation = new MoveNotation();
    private Sides winner;
    private List<OCapturedPiece> OCapturedPieces = new ArrayList<>();

    public ColorType currentColor;


    public Game() {
        this.board = new Board();
        this.currentColor = ColorType.WHITE;
    }

    // make move
}
