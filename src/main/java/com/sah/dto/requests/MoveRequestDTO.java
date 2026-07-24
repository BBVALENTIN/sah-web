package com.sah.dto.requests;

import com.sah.game.dtos.MoveCoords;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveRequestDTO {
    public MoveCoords moveCoords;
    public String lobbyId;
}
