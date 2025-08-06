import { Piesa } from "./piesa.js";

export class Regina extends Piesa{
    constructor(color, row, col, tabla) {
        super(color, row, col);

        this.tip = "REGINA";
        this.tabla = tabla;
        this.img = new Image();

        if(color === "alb")
            this.img.src ="../../images/white-queen.png";
        else
            this.img.src ="../../images/black-queen.png";
    }

    mutariLegale(targetCol, targetRow) {

        console.log("lovestePiese:", this.lovestePiese);
        if(this.peTabla(targetCol, targetRow) && !this.acelasiPatrat(targetCol, targetRow))
            if(targetCol === this.precol || targetRow === this.prerow)
                if(this.patratValid(targetCol, targetRow) && this.piesaInFata(targetCol, targetRow) === false)
                    return true;

        if (this.peTabla(targetCol, targetRow) && !this.acelasiPatrat(targetCol, targetRow)) {
            if (Math.abs(targetCol - this.precol) === Math.abs(targetRow - this.prerow) && this.patratValid(targetCol, targetRow) && !this.piesePeDiagonala(targetCol, targetRow)) {
                return true;
            }
        }
        return false;
    }
}