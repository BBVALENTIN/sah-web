package com.sah.dto;

import com.sah.game.NotatieRocada;
import com.sah.game.piese.Piese;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MoveDataNotationDTO {
    public Piese piesa;
    public int fromRow, fromCol;
    public int targetRow, targetCol;
    public boolean isCheck, isCheckMate, promoted, isCapture;
    public int culoareCurenta;
    public NotatieRocada notatieRocada;
    public List<Piese> oldPieces;
}
