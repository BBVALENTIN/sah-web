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
    this.imageCache = {};
  }


  getPiesa(row, col) {
    return this.piese.find(p => p.row === row && p.col === col);
  }

  redesenare(piese = this.piese) {
    let c = 0;
    const ctx = this.ctx;
    const size = Tabla.squareSize;
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
    // coordonate
    ctx.font = "600 14px Inter, system-ui, -apple-system, BlinkMacSystemFont, sans-serif";
    ctx.fillStyle = "#4a4a4a";

    for (let col = 0; col < 8; col++) {
      const letter = String.fromCharCode(97 + col);
      ctx.fillText(letter, col * size + size - 14, 8 * size - 6);
    }

    for (let row = 0; row < 8; row++) {
      const number = 8 - row;
      ctx.fillText(number, 4, row * size + 14);
    }

    piese.forEach(p => p.desen(this.ctx, p.img));
  }

  setPiecesFromServer(piecesData) {
    this.piese = piecesData.map(p => this.createPiesaFromData(p));
  }

  createPiesaFromData(data) {
    const { tip, color, row, col } = data;
    let piesa;

    switch (tip.toLowerCase()) {
      case "pion":   piesa = new Pion(color, row, col, this); break;
      case "tura":   piesa = new Tura(color, row, col, this); break;
      case "cal":    piesa = new Cal(color, row, col, this); break;
      case "nebun":  piesa = new Nebun(color, row, col, this); break;
      case "regina": piesa = new Regina(color, row, col, this); break;
      case "rege":   piesa = new Rege(color, row, col, this); break;
      default: return null;
    }

    piesa.row = parseInt(row);
    piesa.col = parseInt(col);

    if (piesa.getX && piesa.getY) {
      piesa.x = piesa.getX(piesa.col);
      piesa.y = piesa.getY(piesa.row);
    }


    const colorStr = (color === 1) ? "white" : "black";
    const imgKey = `${colorStr}-${tip.toLowerCase()}`;

    if (this.imageCache[imgKey]) {
      piesa.img = this.imageCache[imgKey];
    } else {
      if (piesa.img) {
        this.imageCache[imgKey] = piesa.img;
      }
    }

    if (piesa.img && !piesa.img.complete) {
      if (!piesa.img.hasRedrawListener) {
        piesa.img.addEventListener('load', () => {
          this.redesenare();
        });
        piesa.img.hasRedrawListener = true;
      }
    }

    return piesa;
  }
}
