import {Piesa} from "./piese/Piesa.js";
import {Pion} from "./piese/Pion.js";
import {Nebun} from "./piese/Nebun.js";
import {Cal} from "./piese/Cal.js";
import {Tura} from "./piese/Tura.js";
import {Rege} from "./piese/Rege.js";
import {Regina} from "./piese/Regina.js";
import {culoriPiesa, TipPiesa} from "./Enums.js";

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
    isBlack: boolean;

    setLastMove(fromRow: number, fromCol: number, toRow: number, toCol: number) {
        this.lastMove = { fromRow, fromCol, toRow, toCol };
    }

    constructor(ctx: CanvasRenderingContext2D) {
        this.ctx = ctx;
        this.rows = 8;
        this.cols = 8;
        this.piese = [];
        this.imageCache = {};
        this.isBlack = false;
    }

    getPiesa(row: number, col: number): Piesa | undefined {
        return this.piese.find(p => p.row === row && p.col === col);
    }

    setOrientare(isBlack: boolean) {
        this.isBlack = isBlack;
    }

    getOrientare() {
        return this.isBlack;
    }

    async loadImages(): Promise<void> {
        const tipuri = Object.values(TipPiesa);
        const culori = [ "white", "black"];
        const promises: Promise<void>[] = [];

        culori.forEach(culoare => {
            tipuri.forEach(tip => {
                const imgKey = `${culoare}-${tip.toLowerCase()}`;

                if (this.imageCache[imgKey]) return;

                const img = new Image();
                img.src = `./images/pieces-default/${imgKey}.png`;

                const p = new Promise<void>((resolve) => {
                    img.onload = () => {
                        this.imageCache[imgKey] = img;
                        resolve();
                    };
                    img.onerror = () => {
                        console.error(`Nu am putut incarca imaginea la: ${img.src}`);
                        resolve(); // Mergem mai departe chiar dacă una lipseste, AIAE
                    };
                });
                promises.push(p);
            });
        });

        await Promise.all(promises);
        console.log("Toate imaginile au fost puse în cache.");
    }

    redesenare(piese: Piesa[] = this.piese, piesaSelectata?: Piesa): void {
        let c = 0;
        const ctx = this.ctx;
        const size = Tabla.squareSize;
        const boardSize = size * 8;

        for(let row = 0; row < this.rows; row++){
            for(let col = 0; col < this.cols; col++)
                {
                    this.ctx.fillStyle = c === 0 ? "#ffffff" : "#0b96be";
                    c = 1 - c;
                    this.ctx.fillRect(col * size, row * size, size, size);
                }
            c = 1 - c;
        }

        this.desenareUltimaMiscare(this.ctx);


        piese.forEach(p => this.deseneazaPiesaOrientata(p));

        if(piesaSelectata && piesaSelectata.img?.complete) {
            this.deseneazaPiesaOrientata(piesaSelectata);
        }

        this.desenareCoordonate();
    }

    private deseneazaPiesaOrientata(piesa: Piesa) {
        const size = Tabla.squareSize;

        const vizualCol = this.isBlack ? 7 - piesa.col : piesa.col;
        const vizualRow = this.isBlack ? 7 - piesa.row : piesa.row;

        if (piesa.isDragging && piesa.dragX !== undefined && piesa.dragY !== undefined) {
            piesa.desen(this.ctx);
        } else {
            const x = vizualCol * size;
            const y = vizualRow * size;

            if (piesa.img && piesa.img.complete) {
                this.ctx.drawImage(piesa.img, x, y, size, size);
            }
        }
    }

    private desenareCoordonate() {
        const ctx = this.ctx;
        const size = Tabla.squareSize;
        ctx.font = "600 14px Inter, sans-serif";
        ctx.fillStyle = "#4a4a4a";

        for (let i = 0; i < 8; i++) {
            const letter = String.fromCharCode(97 + (this.isBlack ? 7 - i : i));
            const number = this.isBlack ? i + 1 : 8 - i;

            ctx.fillText(letter, i * size + size - 14, 8 * size - 6);
            ctx.fillText(number.toString(), 4, i * size + 14);
        }
    }
    createPiesaFromData(data: any):Piesa
    {
        const { tip, color, row, col } = data;
        let piesa: Piesa;

        switch (tip.toLowerCase()) {
            case "pawn": piesa = new Pion(color, row, col); break;
            case "queen": piesa = new Regina(color, row, col); break;
            case "king": piesa = new Rege(color, row, col); break;
            case "rook": piesa = new Tura(color, row, col); break;
            case "bishop": piesa = new Nebun(color, row, col); break;
            case "knight": piesa = new Cal(color, row, col); break;
            default: throw new Error("Tip necunoscut");
        }

        const colorKey = (color === culoriPiesa.ALB) ? "white" : "black"; // pentru load in director
        const imgKey = `${colorKey}-${tip.toLowerCase()}`;

        if (this.imageCache[imgKey]) {
            piesa.img = this.imageCache[imgKey];
        }

        return piesa;
    }

    setPiecesFromServer(piecesData: any): void {
        this.piese = piecesData.map((p:any) => this.createPiesaFromData(p));
    }

    desenareUltimaMiscare(ctx: CanvasRenderingContext2D) {
        const size = Tabla.squareSize;
        if(!this.lastMove) return;

        let {fromRow, fromCol, toRow, toCol} = this.lastMove;

        if(this.isBlack) {
            fromRow = 7 - fromRow;
            fromCol = 7 - fromCol;
            toRow = 7 - toRow;
            toCol = 7 - toCol;
        }

        this.drawHighlight(ctx, fromRow, fromCol);
        this.drawHighlight(ctx, toRow, toCol);
    }

    drawHighlight(ctx: CanvasRenderingContext2D, row: number, col: number) {
        ctx.fillStyle = 'rgba(255, 255, 0, 0.4)';
        const size = Tabla.squareSize;
        ctx.fillRect(col*size, row*size, size, size);
    }
}