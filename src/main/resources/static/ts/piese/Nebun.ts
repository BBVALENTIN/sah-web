import {Piesa} from "./Piesa.js";
import {Tip} from "../Tip.js";

export class Nebun extends Piesa {
    readonly tip: Tip
    img: HTMLImageElement;

    constructor(color: 1 | -1, row:number, col:number) {
        super(color, row, col);
        this.tip = Tip.NEBUN;
        this.img = new Image();


        if(color === 1)
            this.img.src ="../../images/white-bishop.png";
        else
            this.img.src ="../../images/black-bishop.png";
    }
}