import {Piesa} from "./Piesa";
import {Tip} from "../Tip";

export class Regina extends Piesa {
    readonly tip: Tip;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row, col) {
        super(color, row, col);
        this.tip = Tip.REGINA;

        if(color === 1)
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }
}