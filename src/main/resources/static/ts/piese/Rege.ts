import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../tools/Enums.js";

export class Rege extends Piesa {
    readonly tip: TipPiesa = TipPiesa.KING;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
    }
}