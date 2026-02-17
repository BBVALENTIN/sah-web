import {Piesa} from "./Piesa.js";
import {TipPiesa} from "../Enums.js";

export class Regina extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = TipPiesa.REGINA;
        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }
}