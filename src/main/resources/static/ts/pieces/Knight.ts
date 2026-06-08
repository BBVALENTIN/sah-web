import {Piece} from "./Piece.js";
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class Knight extends Piece {
    readonly tip: TipPiesa = TipPiesa.KNIGHT;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}