package com.sah.game;

import com.sah.dto.MoveDataNotationDTO;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.NotatieRocada;
import com.sah.game.GameEnums.Tip;
import com.sah.game.piese.Piese;

public class MoveNotation {
    public String allFormattedMoves, currentFormattedMove;
    public int numberOfMoves;

    public MoveNotation(){
        this.allFormattedMoves = "";
        this.currentFormattedMove = "";
    }

    public MoveNotation(int numberOfMoves, String currentFormattedMove, String allFormattedMoves){
        this.currentFormattedMove = currentFormattedMove;
        this.allFormattedMoves = allFormattedMoves;
    }

    public String formatMove(MoveDataNotationDTO dto){
        if(dto.notatieRocada != null)
        {
            if(dto.notatieRocada == NotatieRocada.MARE)
                return "O-O-O";
            else
                return "O-O";
        }

        char pieceChar;
        pieceChar = Character.toUpperCase(getCharFromTip(dto.piesa.tip));

        char colChar = (char)('a'+dto.targetCol);
        int boardRow = 8 - dto.targetRow;

        char fromColChar = (char)('a'+dto.fromCol);
        int fromBoardRow = 8 - dto.fromRow;

        String disambiguation = "";
        if(dto.piesa.tip != Tip.PAWN)
        {
            for(Piese p: dto.oldPieces)
            {
                if(p == dto.piesa)
                    continue;
                if((p.color == dto.piesa.color && dto.piesa.tip == p.tip) && p.poateAjunge(dto.targetRow, dto.targetCol))
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

        if(dto.piesa.tip == Tip.PAWN) {
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
            if(dto.culoareCurenta == ColorType.ALB)
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
            numberOfMoves++;
    }

    public String generateFEN(Piese[][] board, ColorType culoareCurenta, short halfMove) {
        StringBuilder fen = new StringBuilder();

        for (int r = 0; r < 8; r++) {
            int emptySquares = 0;
            for (int c = 0; c < 8; c++) {
                Piese piese = board[r][c];
                if (piese == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    char type = getCharFromTip(piese.tip);
                    fen.append(piese.color == ColorType.ALB ? Character.toUpperCase(type) : Character.toLowerCase(type));
                }
            }
            if (emptySquares > 0) fen.append(emptySquares);
            if (r < 7) fen.append("/");
        }

        String turn = culoareCurenta == ColorType.ALB ? "w" : "b";
        String enPassant = "-";
        String castling = "KQkq";

        int fullMove = (numberOfMoves / 2) + 1;

        return String.format("%s %s %s %s %d %d",
                fen.toString(), turn, castling, enPassant, halfMove, fullMove);
    }

    private char getCharFromTip(Tip tip) {
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

    public String getAllFormattedMoves() {
        return allFormattedMoves;
    }

    public String getCurrentFormattedMove() {
        return currentFormattedMove;
    }
}
