package com.sah.game;

import com.sah.game.GameEnums.ErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidMoveException extends Exception{
    private final ErrorCodes errorCodes;

}
