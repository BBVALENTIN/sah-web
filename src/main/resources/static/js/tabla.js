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
      default:
        console.error("Tip piesă necunoscut:", tip);
        return null;
    }
    piesa.row = parseInt(row);
    piesa.col = parseInt(col);

    // Setăm imaginea automat dacă e null
    if (!piesa.img) {
      piesa.img = new Image();
      const folder = "../../images/";
      const colorStr = (color === 1) ? "white" : "black";
      piesa.img.src = `${folder}${colorStr}-${tip.toLowerCase()}.png`;
    }

    return piesa;
  }

  drawPiesa(p) {
    if (p) {
      p.desen(this.ctx, p.img);
    }
  }


}
