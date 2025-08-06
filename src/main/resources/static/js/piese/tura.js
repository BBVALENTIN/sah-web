import { Piesa } from "./piesa.js";

export class Tura extends Piesa{
    constructor(color, row, col) {
        super(color, row, col);

        this.tip = "TURA";

        this.img = new Image();
        if(color === "alb")
            this.img.src = "../../images/white-rook.png";
        else
            this.img.src = "../../images/black-rook.png";
    }

    mutariLegale(targetCol, targetRow) {
        if(this.peTabla(targetCol, targetRow) && !this.acelasiPatrat(targetCol, targetRow))
            if(targetCol === this.precol || targetRow === this.prerow)
                if(this.patratValid(targetCol, targetRow) && this.piesaInFata(targetCol, targetRow) === false)
                    return true;
        return false;
    }
}