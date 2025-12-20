package com.sah.dto;

import com.sah.game.piese.Piese;

public class PiesaDTO {
    private String tip;
    private int color;
    private int row;
    private int col;

    public PiesaDTO(String tip, int color, int row, int col) {
        this.tip = tip;
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public String getTip() { return tip;}
    public int getColor() { return color;}
    public int getRow() { return row;}
    public int getCol() { return col;}
}
