import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export class Knight extends Piece {
    readonly tip: PieceType = PieceType.KNIGHT;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}