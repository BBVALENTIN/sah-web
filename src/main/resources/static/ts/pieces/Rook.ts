import {Piece} from "./Piece.js";
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class Rook extends Piece {
    readonly tip: TipPiesa = TipPiesa.ROOK;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}