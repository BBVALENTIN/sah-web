package com.sah.game;

import com.sah.dto.MoveDataNotationDTO;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.NotatieRocada;
import com.sah.game.GameEnums.Tip;
import com.sah.game.piese.Piese;

import java.awt.*;

public class MoveNotation {
    public String allFormattedMoves, currentFormattedMove;

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
        switch(dto.piesa.tip){
            case CAL -> pieceChar = 'N';
            case NEBUN -> pieceChar = 'B';
            case REGE ->  pieceChar = 'K';
            case REGINA -> pieceChar = 'Q';
            case TURA ->  pieceChar = 'R';
            default -> pieceChar = '?';
        }

        char colChar = (char)('a'+dto.targetCol);
        int boardRow = 8 - dto.targetRow;

        char fromColChar = (char)('a'+dto.fromCol);
        int fromBoardRow = 8 - dto.fromRow;

        String disambiguation = "";
        if(dto.piesa.tip != Tip.PION)
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

        if(dto.piesa.tip == Tip.PION) {
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
    }

    public String getAllFormattedMoves() {
        return allFormattedMoves;
    }

    public String getCurrentFormattedMove() {
        return currentFormattedMove;
    }
}
