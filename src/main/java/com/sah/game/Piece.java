package com.sah.game;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class Piece {
    public ColorType color;
    public boolean moved;
    public Type type;

    public Piece(ColorType color, Type type)
    {
        this.color = color;
        this.type = type;
        this.moved = false;
    }

    public static Piece of(ColorType color, Type type){
        return new Piece(color, type);
    }

    public Piece copy() {
        Piece p = Piece.of(color, type);
        p.moved = this.moved;
        return p;
    }
}
