import {Piece} from "./Piece.js";
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class Queen extends Piece {
    readonly tip: TipPiesa = TipPiesa.QUEEN;
    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}