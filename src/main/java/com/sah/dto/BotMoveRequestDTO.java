package com.sah.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BotMoveRequestDTO {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private String gameId;
}
