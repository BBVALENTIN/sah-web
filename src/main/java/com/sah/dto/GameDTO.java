package com.sah.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO
{
    private String lobbyId;
    private int userBlackId;
    private int userWhiteId;
    private int numberOfMoves;
    private String result;
    private String pgn;
}
