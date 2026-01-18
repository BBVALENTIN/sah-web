import { Piesa} from "./piese/Piesa";

export class Tabla {
    static squareSize: number = 100;

    ctx: CanvasRenderingContext2D;
    rows: number;
    cols: number;
    piese: Piesa[];
    imageCache: Record<string, HTMLImageElement>

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

    redesenare(piese: Piesa[] = this.piese): void {
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

        piese.forEach(p => p.desen(this.ctx, p.img));
    }
}