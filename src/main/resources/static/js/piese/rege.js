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

    mutariLegale(targetCol, targetRow) {
        if(this.peTabla(targetCol, targetRow) && !this.acelasiPatrat(targetCol, targetRow))
        {
            if(Math.abs(targetCol-this.precol) + Math.abs(targetRow-this.prerow) === 1 || Math.abs(targetCol-this.precol) * Math.abs(targetRow-this.prerow) === 1)
                if(this.patratValid(targetCol, targetRow));
                    return true;
        }
        return false;
    }
}