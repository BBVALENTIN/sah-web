import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Cal extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);

        this.tip = TipPiesa.CAL;
        this.img = new Image();

        if(color === culoriPiesa.ALB)
            this.img.src ="../../images/white-knight.png";
        else
            this.img.src ="../../images/black-knight.png";
    }
}