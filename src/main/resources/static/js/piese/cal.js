import {Piesa} from "./piesa.js";

export class Cal extends Piesa{
    constructor(color, row, col, tabla) {
        super(color, row, col);

        this.tip = "CAL";

        this.tabla = tabla;

        this.img = new Image();

        if(color === "alb")
            this.img.src ="../../images/white-knight.png";
        else
            this.img.src ="../../images/black-knight.png";
    }

    mutariLegale(targetCol, targetRow) {
        console.log("pe tabla:", this.peTabla(targetCol, targetRow))
        console.log("acelasiPatrat:", this.acelasiPatrat(targetCol, targetRow))
        console.log("ecuatie:", Math.abs(targetCol-this.precol)* Math.abs(targetRow-this.prerow))
        console.log("patratValid:", this.patratValid(targetCol, targetRow));
        if(this.peTabla(targetCol, targetRow) === true && this.acelasiPatrat(targetCol, targetRow) === false){
            if(Math.abs(targetCol-this.precol)* Math.abs(targetRow-this.prerow) === 2)
                if(this.patratValid(targetCol, targetRow) === true)
                return true;
        }
        return false;
    }
}