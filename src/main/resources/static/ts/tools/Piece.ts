import {Board} from "./Board.js";
import {PieceType, SidesExplicit} from "./Enums.js";

export class Piece {
    color: SidesExplicit;
    row:number;
    col: number;
    x: number = 0;
    y: number = 0;
    isDragging?: boolean;
    dragX?: number;
    dragY?: number;
    img?: HTMLImageElement

    constructor(public readonly tip: PieceType,color: SidesExplicit, row: number, col: number) {
        this.tip = tip;
        this.color = color
        this.row = row;
        this.col = col;
    }

    draw(board: Board, ctx: CanvasRenderingContext2D): void {
        if(!this.img || !this.img.complete) return;

        let xPixeli = this.col * board.getSquareSize();
        let yPixeli = this.row * board.getSquareSize();

        if (this.isDragging && this.dragX !== undefined && this.dragY !== undefined) {
            xPixeli = this.dragX;
            yPixeli = this.dragY;
        }

        ctx.drawImage(this.img, xPixeli, yPixeli, board.getSquareSize(), board.getSquareSize());
    }
}