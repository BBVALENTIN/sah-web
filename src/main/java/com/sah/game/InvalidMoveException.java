package com.sah.game;

import com.sah.game.GameEnums.ErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class InvalidMoveException extends Exception{
    private final ErrorCodes errorCodes;

    public InvalidMoveException(ErrorCodes errorCodes) {
        super(errorCodes.name());
        this.errorCodes = errorCodes;
    }
}
