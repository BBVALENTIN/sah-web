import {Piece} from "./Piece.js";
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class Bishop extends Piece {
    readonly tip: TipPiesa = TipPiesa.BISHOP
    constructor(color: SidesExplicit, row:number, col:number) {
        super(color, row, col);
    }
}