import { Pion } from './piese/pion.js';
import { Tura } from './piese/tura.js';
import { Regina} from "./piese/regina.js";
import { Cal } from "./piese/cal.js";
import { Nebun } from "./piese/nebun.js";
import {Rege} from "./piese/rege.js";

export class Tabla {
  static squareSize = 100;

  constructor(ctx) {
    this.ctx = ctx;
    this.rows = 8;
    this.cols = 8;
    this.piese = [];
    this.simPiese = [];
  }

  drawTabla() {
    let c = 0;
    for (let row = 0; row < this.rows; row++) {
      for (let col = 0; col < this.cols; col++) {
        if (c === 0) {
          this.ctx.fillStyle = "#ffffff";
          c = 1;
        } else {
          this.ctx.fillStyle = "#0b96be";
          c = 0;
        }
        this.ctx.fillRect(
            col * Tabla.squareSize,
            row * Tabla.squareSize,
            Tabla.squareSize,
            Tabla.squareSize
        );
      }
      c = c === 0 ? 1 : 0;
    }

    // Desenăm piesele peste tabla
    this.piese.forEach(piesa => {
      piesa.desen(this.ctx, piesa.img);
    });
  }

  setPiese() {
    const pieseMajore = [Tura, Cal, Nebun, Regina, Rege, Nebun, Cal, Tura];

    for(let col = 0; col < 8; col++)
    {
      this.piese.push(new Pion("alb", 6, col, this));
      this.piese.push(new Pion("negru", 1, col, this));
    }

    for(let col = 0; col < 8; col++)
    {
      const PiesaCurenta = pieseMajore[col];
      this.piese.push(new PiesaCurenta("alb", 7, col, this));
      this.piese.push(new PiesaCurenta("negru", 0, col, this));
    }
  }

  getPiesa(row, col) {
    return this.piese.find(p => p.row === row && p.col === col);
  }

  redesenare() {
    let c = 0;
    for (let row = 0; row < this.rows; row++) {
      for (let col = 0; col < this.cols; col++) {
        this.ctx.fillStyle = (c === 0) ? "#ffffff" : "#0b96be";
        c = 1 - c;
        this.ctx.fillRect(
            col * Tabla.squareSize,
            row * Tabla.squareSize,
            Tabla.squareSize,
            Tabla.squareSize
        );
      }
      c = 1 - c;
    }

    this.piese.forEach(p => p.desen(this.ctx, p.img));
  }
}
