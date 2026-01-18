import {Tabla} from "../Tabla.js";
import {Tip} from "../Tip";

export class Piesa {
    readonly tip: Tip;
    color: 1 | -1;
    row:number;
    col: number;
    x: number;
    y: number;
    isDragging?: boolean;
    dragX?: number;
    dragY?: number;
    constructor(color: 1 | -1, row: number, col: number) {
        this.color = color
        this.row = row;
        this.col = col;
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
}