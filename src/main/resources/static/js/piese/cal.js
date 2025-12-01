import {Piesa} from "./piesa.js";

export class Cal extends Piesa{
    constructor(color, row, col, tabla) {
        super(color, row, col);

        this.tip = "CAL";
        this.tabla = tabla;

        this.img = new Image();

        if(color === 1)
            this.img.src ="../../images/white-knight.png";
        else
            this.img.src ="../../images/black-knight.png";
    }
}