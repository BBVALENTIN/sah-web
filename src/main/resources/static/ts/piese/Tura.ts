import {Piesa} from "./Piesa.js";
import {TipPiesa} from "../Enums.js";

export class Tura extends Piesa {
    readonly tip: TipPiesa;
    img: HTMLImageElement;

    constructor(color: 1 | -1, row: number, col: number) {
        super(color, row, col);
        this.tip = TipPiesa.TURA;
        this.img = new Image();

        if(color === 1)
            this.img.src = "../../images/white-rook.png";
        else
            this.img.src = "../../images/black-rook.png";
    }
}