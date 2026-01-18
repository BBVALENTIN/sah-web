import { Piesa } from "./piesa.js";

export class Regina extends Piesa{
    constructor(color, row, col) {
        super(color, row, col);

        this.tip = "REGINA";
        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }
}