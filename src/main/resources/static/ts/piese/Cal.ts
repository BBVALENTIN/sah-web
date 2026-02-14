import {Piesa} from "./Piesa.js";
import {Enums} from "../Enums";

export class Cal extends Piesa {
    readonly tip: Enums;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);

        this.tip = Enums.CAL;
        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-knight.png";
        else
            this.img.src ="../../images/black-knight.png";
    }
}