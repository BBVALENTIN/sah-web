package com.sah.dto;

import com.sah.enums.Sides;
import com.sah.game.GameEnums.ColorType;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotMinimalStateDTO {
    private List<PiesaDTO> Piese;
    private ColorType culoareCurenta;
    private String currentPGN;
    private Sides botSide;
    private String currentFEN;
}
