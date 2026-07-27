package com.sah.game;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public record Move(int fromRow, int fromCol, int targetRow, int targetCol, Type pieceType, ColorType pieceColor) {
}
