import {Piece} from "./Piece.js";
import {SidesExplicit, PieceType} from "../tools/Enums.js";

export class Bishop extends Piece {
    readonly tip: PieceType = PieceType.BISHOP
    constructor(color: SidesExplicit, row:number, col:number) {
        super(color, row, col);
    }
}