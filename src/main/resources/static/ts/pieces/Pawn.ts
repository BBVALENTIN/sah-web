import {Piece} from './Piece.js'
import {SidesExplicit, TipPiesa} from "../tools/Enums.js";

export class Pawn extends Piece {
    readonly tip: TipPiesa = TipPiesa.PAWN;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}