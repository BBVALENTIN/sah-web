package com.sah.game;

import com.sah.dto.chess.CastlingInfoDTO;
import com.sah.dto.chess.FENRequestDTO;
import com.sah.dto.chess.MoveDataNotationDTO;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.CastlingNotation;
import com.sah.game.gameenums.Type;
import com.sah.game.pieces.Pieces;

import java.util.ArrayList;
import java.util.List;

public class MoveNotation {
    public String allFormattedMoves, currentFormattedMove;
    public int movesPlayed;
    public List<String> FENList = new ArrayList<>();
    public String currentFEN;

    public MoveNotation(){
        this.allFormattedMoves = "";
        this.currentFormattedMove = "";
        this.currentFEN = "";
    }

    public MoveNotation(int movesPlayed, String currentFormattedMove, String allFormattedMoves, List<String> FENList, String currentFEN){
        this.currentFormattedMove = currentFormattedMove;
        this.allFormattedMoves = allFormattedMoves;
        this.FENList = FENList;
        this.currentFEN = currentFEN;
    }

    public String formatMove(MoveDataNotationDTO dto){
        if(dto.castlingNotation != null)
        {
            if(dto.castlingNotation == CastlingNotation.BIG)
                return "O-O-O";
            else
                return "O-O";
        }

        char pieceChar;
        pieceChar = Character.toUpperCase(getCharFromTip(dto.piece.type));

        char colChar = (char)('a'+dto.targetCol);
        int boardRow = 8 - dto.targetRow;

        char fromColChar = (char)('a'+dto.fromCol);
        int fromBoardRow = 8 - dto.fromRow;

        String disambiguation = "";
        if(dto.piece.type != Type.PAWN)
        {
            for(Pieces p: dto.oldPieces)
            {
                if(p == dto.piece)
                    continue;
                if((p.color == dto.piece.color && dto.piece.type == p.type) && p.canGetTo(dto.targetRow, dto.targetCol))
                {
                    if(p.col == dto.fromCol) {
                        disambiguation = "" + fromBoardRow;
                    }
                    else {
                        disambiguation = "" + fromColChar;
                    }
                }
            }
        }

        String notation = "";

        if(dto.piece.type == Type.PAWN) {
            if (dto.isCapture)
            {
                notation = fromColChar+"x"+colChar+boardRow;
            }
            else {
                notation = ""+colChar+boardRow;
            }
        }
        else
        {
            if(dto.isCapture)
                notation = "" + pieceChar + disambiguation + "x" + colChar + boardRow;
            else
                notation = "" + pieceChar + disambiguation + colChar + boardRow;
        }

        if(dto.promoted){
            notation = notation + "=Q";
        }
        if(dto.isCheck && !dto.isCheckMate) {
            notation = notation + "+";
        }
        if(dto.isCheckMate)
        {
            if(dto.currentColor == ColorType.WHITE)
                notation = notation + "#" + " 0-1";
            else
                notation = notation + "#" + " 1-0";
        }
        allPGN(notation);
        return notation;
    }

    public void allPGN(String notation)
    {
            currentFormattedMove = notation;
            allFormattedMoves += notation + " ";
            movesPlayed++;
    }

    public String generateFEN(FENRequestDTO req) {
        StringBuilder fen = new StringBuilder();

        for (int r = 0; r < 8; r++) {
            int emptySquares = 0;
            for (int c = 0; c < 8; c++) {
                Pieces piese = req.board[r][c];
                if (piese == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    char type = getCharFromTip(piese.type);
                    fen.append(piese.color == ColorType.WHITE ? Character.toUpperCase(type) : Character.toLowerCase(type));
                }
            }
            if (emptySquares > 0) fen.append(emptySquares);
            if (r < 7) fen.append("/");
        }

        String turn = req.getCurrentColor() == ColorType.WHITE ? "w" : "b";
        String enPassant = "-";
        String castling = calculateCastlingNotation(req.getCastlingInfo());


        int fullMove = (movesPlayed / 2) + 1;
        this.currentFEN = String.format("%s %s %s %s %d %d",
                fen.toString(), turn, castling, enPassant, req.getHalfMove(), fullMove);
        FENList.add(currentFEN);
        return currentFEN;
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

    public void resetNotations() {
        allFormattedMoves = "";
        currentFormattedMove = "";
        this.FENList.clear();
    }

    private String calculateCastlingNotation(CastlingInfoDTO dto) {
        StringBuilder sb = new StringBuilder();

        if(dto.possibleCastleWhiteShort)
            sb.append('K');
        if(dto.possibleCastleWhiteLong)
            sb.append('Q');
        if(dto.possibleCastleBlackShort)
            sb.append('k');
        if(dto.possibleCastleBlackLong)
            sb.append('q');

        if(sb.isEmpty())
            sb.append('-');
        return sb.toString();
    }

    public String getAllFormattedMoves() {
        return allFormattedMoves;
    }

    public String getCurrentFormattedMove() {
        return currentFormattedMove;
    }
    public String getCurrentFEN() { return currentFEN; }
    public List<String> getFENList() { return FENList; }
}
