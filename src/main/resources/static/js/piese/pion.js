import { Piesa } from './piesa.js';

export class Pion extends Piesa {
    constructor(color, row, col, tabla) {
        super(color, row, col, tabla);
        this.tip = "PION";


        this.img = new Image();
        if (color === "alb") {
            this.img.src = "../../images/white-pawn.png";
        } else {
            this.img.src = "../../images/black-pawn.png";
        }
    }

       mutariLegale(targetCol, targetRow)
        {
            console.log("Se verifică mutare:", this.precol, this.prerow, "->", targetCol, targetRow);
            console.log("lovestePiese:", this.lovestePiese);
            console.log("acelasiPatrat:", this.acelasiPatrat(targetCol, targetRow));
            console.log("piesaInFata:", this.piesaInFata ? this.piesaInFata(targetCol, targetRow) : "fara piesaInFata");
            console.log("miscata:", this.miscata);
            let directie;
            if(this.color === "alb")
                directie = -1;
            else
                directie = 1;
            this.lovestePiese = this.getLovesteP(targetCol, targetRow);
            if(targetCol === this.precol && targetRow === this.prerow+directie && this.lovestePiese === null && this.acelasiPatrat(targetCol, targetRow) === false)
                return true;
            if(targetCol === this.precol && targetRow === this.prerow+(directie*2) && this.miscata === false && this.piesaInFata(targetCol, targetRow) === false && this.acelasiPatrat(targetCol, targetRow) === false &&this.lovestePiese===null)
                return true;
            return false;
        }
}
