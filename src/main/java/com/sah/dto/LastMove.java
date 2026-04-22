package com.sah.dto;

import com.sah.game.piese.Piese;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LastMove {
    private int fromRow, fromCol, toRow, toCol;
    private PiesaDTO lastPiece;
}
