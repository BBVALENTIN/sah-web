package com.sah.game;

import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;

public class Piece {
    public ColorType color;
    public boolean moved;
    public Type type;
    protected Game game;

    public Piece(ColorType color, Type type)
    {
        this.color = color;
        this.moved = false;
        this.game = game;
    }

    public static Piece of(ColorType color, Type type){
        return new Piece(color, type);
    }
}
