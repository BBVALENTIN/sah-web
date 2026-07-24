import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export class Queen extends Piece {
    readonly tip: PieceType = PieceType.QUEEN;
    constructor(color: SidesExplicit, row: number, col: number) {
        super(color, row, col);
    }
}