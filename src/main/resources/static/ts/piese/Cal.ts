import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Cal extends Piesa {
    readonly tip: TipPiesa = TipPiesa.KNIGHT;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
    }
}