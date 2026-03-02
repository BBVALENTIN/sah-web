import {Piesa} from "./piese/Piesa.js";
import {Pion} from "./piese/Pion.js";
import {Nebun} from "./piese/Nebun.js";
import {Cal} from "./piese/Cal.js";
import {Tura} from "./piese/Tura.js";
import {Rege} from "./piese/Rege.js";
import {Regina} from "./piese/Regina.js";

export class Tabla {
    static squareSize: number = 75;
    lastMove?: {
        fromRow: number,
        fromCol: number,
        toRow: number,
        toCol: number
    };
    ctx: CanvasRenderingContext2D;
    rows: number;
    cols: number;
    piese: Piesa[];
    imageCache: Record<string, HTMLImageElement>

    setLastMove(fromRow: number, fromCol: number, toRow: number, toCol: number) {
        this.lastMove = { fromRow, fromCol, toRow, toCol };
    }

    constructor(ctx: CanvasRenderingContext2D) {
        this.ctx = ctx;
        this.rows = 8;
        this.cols = 8;
        this.piese = [];
        this.imageCache = {};
    }

    getPiesa(row: number, col: number): Piesa | undefined {
        return this.piese.find(p => p.row === row && p.col === col);
    }

    redesenare(piese: Piesa[] = this.piese, piesaSelectata?: Piesa): void {
        let c = 0;
        const ctx = this.ctx;
        const size = Tabla.squareSize;


        for(let row = 0; row < this.rows; row++){
            for(let col = 0; col < this.cols; col++)
                {
                    this.ctx.fillStyle = c === 0 ? "#ffffff" : "#0b96be";
                    c = 1 - c;
                    this.ctx.fillRect(col * size, row * size, size, size);
                }
            c = 1 - c;
        }

        //highlight ultima miscare
        // this.desenareUltimaMiscare(this.ctx);

        //coordonate
        ctx.font = "600 14px Inter, system-ui, -apple-system, BlinkMacSystemFont, sans-serif";
        ctx.fillStyle = "#4a4a4a";

        for (let col = 0; col < 8; col++) {
            const letter = String.fromCharCode(97 + col);
            ctx.fillText(letter, col * size + size - 14, 8 * size - 6);
        }

        for (let row = 0; row < 8; row++) {
            const number = 8 - row;
            ctx.fillText(number.toString(), 4, row * size + 14);
        }

        piese.forEach(p => p.desen(this.ctx));

        if(piesaSelectata && piesaSelectata.img.complete) {
            piesaSelectata.desen(ctx);
        }
    }

    createPiesaFromData(data: any):Piesa
    {
        const { tip, color, row, col} = data;

        let piesa: Piesa;

        switch (tip.toLowerCase()) {
            case "pion":
                piesa = new Pion(color, row, col);
                break;
            case "tura":
                piesa = new Tura(color, row, col);
                break;
            case "cal":
                piesa = new Cal(color, row, col);
                break;
            case "nebun":
                piesa = new Nebun(color, row, col);
                break;
            case "regina":
                piesa = new Regina(color, row, col);
                break;
            case "rege":
                piesa = new Rege(color, row, col);
                break;
            default:
                throw new Error(`Tip necunoscut ${tip.toLowerCase()}`);
        }

        piesa.row = parseInt(row);
        piesa.col = parseInt(col);

        if (piesa.getX && piesa.getY) {
            piesa.x = piesa.getX(piesa.col);
            piesa.y = piesa.getY(piesa.row);
        }

        const colorStr = (color === 1) ? "white" : "black";
        const imgKey = `${colorStr}-${tip.toLowerCase()}`;
        const imgAny = piesa.img as any;

        if (this.imageCache[imgKey]) {
            piesa.img = this.imageCache[imgKey];
        } else {
            if (piesa.img) {
                this.imageCache[imgKey] = piesa.img;
            }
        }

        if (piesa.img && !piesa.img.complete) {
            if (!imgAny.hasRedrawListener) {
                piesa.img.addEventListener('load', () => {
                    this.redesenare();
                });
                imgAny.hasRedrawListener = true;
            }
        }

        return piesa;
    }

    setPiecesFromServer(piecesData: any): void {
        this.piese = piecesData.map((p:any) => this.createPiesaFromData(p));
    }

    desenareUltimaMiscare(ctx: CanvasRenderingContext2D) {
        const size = Tabla.squareSize;
        if(!this.lastMove) return;

        const {fromRow, fromCol, toRow, toCol} = this.lastMove;

        this.drawHighlight(ctx, fromRow, fromCol);
        this.drawHighlight(ctx, toRow, toCol);
    }

    drawHighlight(ctx: CanvasRenderingContext2D, row: number, col: number) {
        ctx.fillStyle = 'rgba(255, 255, 0, 0.4)';
        const size = Tabla.squareSize;
        console.log(row*size, col*size, size, size);
        ctx.fillRect(col*size, row*size, size, size);
    }

    rotateBoard() {
        this.ctx.translate(this.ctx.canvas.width, this.ctx.canvas.height);
        this.ctx.rotate(Math.PI);
    }
}