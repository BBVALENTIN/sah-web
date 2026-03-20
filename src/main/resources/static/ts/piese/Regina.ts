import {Piesa} from "./Piesa.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Regina extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: culoriPiesa, row: number, col: number) {
        super(color, row, col);
        this.tip = TipPiesa.REGINA;
        this.img = new Image();
        console.log("PIESA REGINA ARE CULOAREA : ", color);
        if(color === culoriPiesa.ALB)
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }
}