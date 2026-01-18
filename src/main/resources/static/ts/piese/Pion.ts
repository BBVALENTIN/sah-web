import {Piesa} from './Piesa'
import {Tip} from "../Tip.js";

export class Pion extends Piesa {
    readonly tip: Tip;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);

        this.tip = Tip.CAL;

        if (color === 1) {
            this.img.src = "../../images/white-pawn.png";
        } else {
            this.img.src = "../../images/black-pawn.png";
        }
    }
}