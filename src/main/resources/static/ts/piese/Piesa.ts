import {Tabla} from "../Tabla.js";
import {culoriPiesa, TipPiesa} from "../Enums.js";

export class Piesa {
    readonly tip: TipPiesa | undefined;
    color: culoriPiesa;
    row:number;
    col: number;
    x: number = 0;
    y: number = 0;
    isDragging?: boolean;
    dragX?: number;
    dragY?: number;
    img?: HTMLImageElement
    constructor(color: culoriPiesa, row: number, col: number) {
        this.color = color
        this.row = row;
        this.col = col;
    }

    desen(ctx: CanvasRenderingContext2D): void {
        if(!this.img || !this.img.complete) return;

        let xPixeli = this.col * Tabla.squareSize;
        let yPixeli = this.row * Tabla.squareSize;

        if (this.isDragging && this.dragX !== undefined && this.dragY !== undefined) {
            xPixeli = this.dragX;
            yPixeli = this.dragY;
        }

        ctx.drawImage(this.img, xPixeli, yPixeli, Tabla.squareSize, Tabla.squareSize);
    }

    getX(col: number): number
    {
        return col*Tabla.squareSize;
    }

    getY(row: number): number
    {
        return row*Tabla.squareSize
    }
}