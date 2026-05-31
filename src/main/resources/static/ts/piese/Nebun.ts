import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../tools/Enums.js";

export class Nebun extends Piesa {
    readonly tip: TipPiesa = TipPiesa.BISHOP
    constructor(color: culoriPiesa, row:number, col:number) {
        super(color, row, col);
    }
}