import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export class King extends Piece {
    readonly tip: PieceType = PieceType.KING;

    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}