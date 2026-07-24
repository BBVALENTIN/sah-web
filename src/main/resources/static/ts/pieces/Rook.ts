import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export class Rook extends Piece {
    readonly tip: PieceType = PieceType.ROOK;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}