import { Piesa } from "./piesa.js";

export class Tura extends Piesa{
    constructor(color, row, col) {
        super(color, row, col);

        this.tip = "TURA";

        this.img = new Image();
        if(color === 1)
            this.img.src = "../../images/white-rook.png";
        else
            this.img.src = "../../images/black-rook.png";
    }
}