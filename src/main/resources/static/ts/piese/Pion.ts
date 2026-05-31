import {Piesa} from './Piesa.js'
import {culoriPiesa, TipPiesa} from "../tools/Enums.js";

export class Pion extends Piesa {
    readonly tip: TipPiesa = TipPiesa.PAWN;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
    }
}