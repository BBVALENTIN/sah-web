import { Piesa } from "./piesa.js";

export class Nebun extends Piesa{
    constructor(color, row, col, tabla) {
        super(color, row, col);

        this.tip = "NEBUN";
        this.tabla = tabla;
        this.img = new Image();

        if(color === "alb")
            this.img.src ="../../images/white-bishop.png";
        else
            this.img.src ="../../images/black-bishop.png";
    }

    mutariLegale(targetCol, targetRow) {
        console.log("pe tabla:", this.peTabla(targetCol, targetRow))
        console.log("acelasiPatrat:", this.acelasiPatrat(targetCol, targetRow))
        console.log("ecuatie:", Math.abs(targetCol-this.precol) === Math.abs(targetRow-this.prerow))
        console.log("patratValid:", this.patratValid(targetCol, targetRow));
        console.log("piesadiagonala:", this.piesePeDiagonala(targetCol, targetRow));

        if (this.peTabla(targetCol, targetRow) && !this.acelasiPatrat(targetCol, targetRow)) {
            if (Math.abs(targetCol - this.precol) === Math.abs(targetRow - this.prerow) && this.patratValid(targetCol, targetRow) && !this.piesePeDiagonala(targetCol, targetRow)) {
                return true;
            }
        }

    }
}