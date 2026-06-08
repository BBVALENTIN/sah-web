package com.sah.dto.chess;

import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PieceDTO {
    private Type type;
    private ColorType color;
    private int row;
    private int col;

    public PieceDTO(Type type, ColorType color, int row, int col) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;
    }
}
