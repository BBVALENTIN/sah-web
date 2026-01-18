export class Tabla {
    static squareSize: number = 100;

    ctx: CanvasRenderingContext2D;
    rows: number;
    cols: number;

    constructor(ctx: CanvasRenderingContext2D) {
        this.ctx = ctx;
        this.rows = 8;
        this.cols = 8;
    }
}