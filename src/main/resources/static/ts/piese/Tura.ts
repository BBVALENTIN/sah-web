import {Piesa} from "./Piesa.js";
import {Enums} from "../Enums.js";

export class Tura extends Piesa {
    readonly tip: Enums;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = Enums.TURA;
        this.img = new Image();

        if(color === 1)
            this.img.src = "../../images/white-rook.png";
        else
            this.img.src = "../../images/black-rook.png";
    }
}