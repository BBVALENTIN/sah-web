package com.sah.dto;

import com.sah.game.GameEnums.ColorType;
import com.sah.game.piese.Piese;

import java.awt.*;

public class PiesaDTO {
    private String tip;
    private ColorType color;
    private int row;
    private int col;

    public PiesaDTO(String tip, ColorType color, int row, int col) {
        this.tip = tip;
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public String getTip() { return tip;}
    public ColorType getColor() { return color;}
    public int getRow() { return row;}
    public int getCol() { return col;}
}
