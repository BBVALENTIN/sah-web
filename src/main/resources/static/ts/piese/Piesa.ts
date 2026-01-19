import {Tabla} from "../Tabla.js";
import {Tip} from "../Tip.js";

export class Piesa {
    readonly tip: Tip | undefined;
    color: 1 | -1;
    row:number;
    col: number;
    x: number = 0;
    y: number = 0;
    isDragging?: boolean;
    dragX?: number;
    dragY?: number;
    img: HTMLImageElement
    constructor(color: 1 | -1, row: number, col: number) {
        this.color = color
        this.row = row;
        this.col = col;
        this.img = new Image();
    }

    desen(ctx: CanvasRenderingContext2D, imagine?: HTMLImageElement): void {
        if(!imagine) return;

        let xPixeli: number, yPixeli: number;

        if(this.isDragging && this.dragX !== undefined && this.dragY !== undefined) {
            xPixeli = this.dragX;
            yPixeli = this.dragY;
        }
        else {
            xPixeli = this.col * Tabla.squareSize;
            yPixeli = this.row * Tabla.squareSize;
        }

        if(imagine.complete) {
            ctx.drawImage(imagine, xPixeli, yPixeli, Tabla.squareSize, Tabla.squareSize);
        }
        else {
            imagine.onload = () => {
                ctx.drawImage(imagine, xPixeli, yPixeli, Tabla.squareSize, Tabla.squareSize);
            }
        }
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