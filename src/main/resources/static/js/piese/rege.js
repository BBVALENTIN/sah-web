import { Piesa } from "./piesa.js";

export class Rege extends Piesa{
    constructor(color, row, col) {
        super(color, row, col);

        this.tip = "REGE";

        this.img = new Image();
        if(this.color === "alb")
            this.img.src="../../images/white-king.png";
        else
            this.img.src="../../images/black-king.png";
    }
}