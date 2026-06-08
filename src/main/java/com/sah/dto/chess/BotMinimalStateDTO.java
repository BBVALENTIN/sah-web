package com.sah.dto.chess;

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
    private List<PieceDTO> Pieces;
    private ColorType currentColor;
    private String currentPGN;
    private Sides botSide;
    private String currentFEN;
}
