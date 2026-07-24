package com.sah.game.dtos;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OCapturedPiece {
    public Type pieceType;
    public ColorType colorType;
}
