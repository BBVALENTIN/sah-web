package com.sah.game.exceptions;

import com.sah.game.gameenums.ErrorCodes;
import lombok.Getter;

@Getter
public class InvalidMoveException extends Exception{
    private final ErrorCodes errorCodes;

    public InvalidMoveException(ErrorCodes errorCodes) {
        super(errorCodes.name());
        this.errorCodes = errorCodes;
    }
}
