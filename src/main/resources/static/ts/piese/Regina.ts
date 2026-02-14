import {Piesa} from "./Piesa.js";
import {Enums} from "../Enums";

export class Regina extends Piesa {
    readonly tip: Enums;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = Enums.REGINA;
        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }
}