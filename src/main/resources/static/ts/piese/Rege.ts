import {Piesa} from "./Piesa.js";
import {Enums} from "../Enums";

export class Rege extends Piesa {
    readonly tip: Enums;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = Enums.REGE;
        this.img = new Image();

        if(this.color === 1)
            this.img.src="../../images/white-king.png";
        else
            this.img.src="../../images/black-king.png";
    }
}