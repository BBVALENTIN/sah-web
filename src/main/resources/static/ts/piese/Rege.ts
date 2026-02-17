import {Piesa} from "./Piesa.js";
import {TipPiesa} from "../Enums.js";

export class Rege extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = TipPiesa.REGE;
        this.img = new Image();

        if(this.color === 1)
            this.img.src="../../images/white-king.png";
        else
            this.img.src="../../images/black-king.png";
    }
}