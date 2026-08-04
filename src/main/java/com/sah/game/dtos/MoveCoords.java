package com.sah.game.dtos;

import com.sah.game.gameenums.PromotionPiece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveCoords {
    private int fromRow, fromCol, targetRow, targetCol;
    private PromotionPiece promotionPiece;
}
