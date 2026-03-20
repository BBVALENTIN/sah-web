import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Regina extends Piesa {
    readonly tip: TipPiesa = TipPiesa.QUEEN;
    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
    }
}