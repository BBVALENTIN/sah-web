import { Piesa } from "./piesa.js";

export class Nebun extends Piesa{
    constructor(color, row, col) {
        super(color, row, col);

        this.tip = "NEBUN";
        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-bishop.png";
        else
            this.img.src ="../../images/black-bishop.png";
    }
}