package com.sah.dto.chess;

import com.sah.enums.Sides;
import com.sah.game.gameenums.ColorType;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BotMinimalStateDTO {
    private ColorType currentColor;
    private String currentPGN;
    private Sides botSide;
    private String currentFEN;
}
