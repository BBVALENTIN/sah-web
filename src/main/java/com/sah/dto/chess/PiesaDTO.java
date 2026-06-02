package com.sah.dto.chess;

import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Tip;

public class PiesaDTO {
    private Tip tip;
    private ColorType color;
    private int row;
    private int col;

    public PiesaDTO(Tip tip, ColorType color, int row, int col) {
        this.tip = tip;
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public Tip getTip() { return tip;}
    public ColorType getColor() { return color;}
    public int getRow() { return row;}
    public int getCol() { return col;}
}
