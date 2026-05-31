import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../tools/Enums.js";

export class Tura extends Piesa {
    readonly tip: TipPiesa = TipPiesa.ROOK;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
    }
}