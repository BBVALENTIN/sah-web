import { Piesa } from './piesa.js';

export class Pion extends Piesa {
    constructor(color, row, col) {
        super(color, row, col);
        this.tip = "PION";


        this.img = new Image();
        if (color === 1) {
            this.img.src = "../../images/white-pawn.png";
        } else {
            this.img.src = "../../images/black-pawn.png";
        }
    }
}
