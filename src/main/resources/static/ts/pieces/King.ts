import {Piece} from "./Piece.js";
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class King extends Piece {
    readonly tip: TipPiesa = TipPiesa.KING;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}