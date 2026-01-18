import {Piesa} from "./Piesa";
import {Tip} from "../Tip";

export class Cal extends Piesa {
    readonly tip: Tip;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row, col) {
        super(color, row, col);

        this.tip = Tip.CAL;

        if(color === 1)
            this.img.src ="../../images/white-knight.png";
        else
            this.img.src ="../../images/black-knight.png";
    }
}