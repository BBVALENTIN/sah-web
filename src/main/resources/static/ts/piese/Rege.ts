import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Rege extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
        this.tip = TipPiesa.REGE;
        this.img = new Image();

        if(this.color === culoriPiesa.ALB)
            this.img.src="../../images/white-king.png";
        else
            this.img.src="../../images/black-king.png";
    }
}