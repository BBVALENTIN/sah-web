package com.sah.dto.chess;


import com.sah.game.GameEnums.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MinimalStateDTO {
    public List<PiesaDTO> Piese;
    public ColorType culoareCurenta; // de facut enum pentru culoare
    public String currentPGN;
}
