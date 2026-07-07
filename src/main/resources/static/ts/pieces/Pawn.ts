import {Piece} from './Piece.js'
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export class Pawn extends Piece {
    readonly tip: PieceType = PieceType.PAWN;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}