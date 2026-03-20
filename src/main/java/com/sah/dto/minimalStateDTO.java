package com.sah.dto;


import com.sah.game.GameEnums.ColorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class minimalStateDTO {
    public List<PiesaDTO> Piese;
    public ColorType culoareCurenta; // de facut enum pentru culoare
    public String currentPGN;
}
