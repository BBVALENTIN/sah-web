package com.sah.dto.requests;

import com.sah.game.dtos.MoveCoords;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BotMoveRequestDTO {
    private MoveCoords moveCoords;
    private String gameId;
}
