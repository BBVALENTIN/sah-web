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
    public List<PieceDTO> Pieces;
    public ColorType currentColor;
    public String currentPGN;
}
