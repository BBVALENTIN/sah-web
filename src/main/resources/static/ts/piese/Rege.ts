import {Piesa} from "./Piesa";
import {Tip} from "../Tip";

export class Rege extends Piesa {
    readonly tip: Tip;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = Tip.REGE;
        if(this.color === 1)
            this.img.src="../../images/white-king.png";
        else
            this.img.src="../../images/black-king.png";
    }
}