import {Piece} from "../pieces/Piece.js";
import {Pawn} from "../pieces/Pawn.js";
import {Bishop} from "../pieces/Bishop.js";
import {Knight} from "../pieces/Knight.js";
import {Rook} from "../pieces/Rook.js";
import {King} from "../pieces/King.js";
import {Queen} from "../pieces/Queen.js";
import {Sides, SidesExplicit, TipPiesa} from "./Enums.js";
import {PieceDTO} from "./Types.js";

export class Board {
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
    pieces: Piece[];
    imageCache: Record<string, HTMLImageElement>
    isBlack: boolean;

    setLastMove(fromRow: number, fromCol: number, toRow: number, toCol: number) {
        this.lastMove = { fromRow, fromCol, toRow, toCol };
    }

    constructor(ctx: CanvasRenderingContext2D) {
        this.ctx = ctx;
        this.rows = 8;
        this.cols = 8;
        this.pieces = [];
        this.imageCache = {};
        this.isBlack = false;
    }

    getPiece(row: number, col: number): Piece  | undefined {
        return this.pieces.find(p => p.row === row && p.col === col);
    }

    setOrientation(isBlack: boolean) {
        this.isBlack = isBlack;
    }

    getOrientation() {
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
                        resolve();
                    };
                });
                promises.push(p);
            });
        });

        await Promise.all(promises);
        console.log("All images have been stocked in cache.");
    }

    redraw(pieces: Piece[] = this.pieces, selectedPiece?: Piece): void {
        let c = 0;
        const ctx = this.ctx;
        const size = Board.squareSize;
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

        this.drawLastMove(this.ctx);


        pieces.forEach(p => this.drawOrientedPiece(p));

        if(selectedPiece && selectedPiece.img?.complete) {
            this.drawOrientedPiece(selectedPiece);
        }

        this.drawCoordinates();
    }

    private drawOrientedPiece(piece: Piece) {
        const size = Board.squareSize;

        if (piece.isDragging && piece.dragX !== undefined && piece.dragY !== undefined) {
            piece.draw(this.ctx);
            return;
        }

        const visualCol = this.isBlack ? 7 - piece.col : piece.col;
        const visualRow = this.isBlack ? 7 - piece.row : piece.row;

        if (piece.isDragging && piece.dragX !== undefined && piece.dragY !== undefined) {
            piece.draw(this.ctx);
        } else {
            const x = visualCol * size;
            const y = visualRow * size;

            if (piece.img && piece.img.complete) {
                this.ctx.drawImage(piece.img, x, y, size, size);
            }
        }
    }

    private drawCoordinates() {
        const ctx = this.ctx;
        const size = Board.squareSize;
        ctx.font = "600 14px Fira Code, sans-serif";
        ctx.fillStyle = "#4a4a4a";

        for (let i = 0; i < 8; i++) {
            const letter = String.fromCharCode(97 + (this.isBlack ? 7 - i : i));
            const number = this.isBlack ? i + 1 : 8 - i;

            ctx.fillText(letter, i * size + size - 14, 8 * size - 6);
            ctx.fillText(number.toString(), 4, i * size + 14);
        }
    }

    createPieceFromData(data: any): Piece
    {
        const { type, color, row, col } = data;

        let piece: Piece;

        switch (type.toLowerCase()) {
            case "pawn": piece = new Pawn(color, row, col); break;
            case "queen": piece = new Queen(color, row, col); break;
            case "king": piece = new King(color, row, col); break;
            case "rook": piece = new Rook(color, row, col); break;
            case "bishop": piece = new Bishop(color, row, col); break;
            case "knight": piece = new Knight(color, row, col); break;
            default: throw new Error("Unknown piece type");
        }

        const colorKey = (color === SidesExplicit.WHITE) ? "white" : "black";
        const imgKey = `${colorKey}-${type.toLowerCase()}`;

        if (this.imageCache[imgKey]) {
            piece.img = this.imageCache[imgKey];
        }

        return piece;
    }

    setPiecesFromServer(piecesData: PieceDTO[]): void {
        this.pieces = piecesData.map((p:any) => this.createPieceFromData(p));
    }

    drawLastMove(ctx: CanvasRenderingContext2D) {
        const size = Board.squareSize;
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
        const size = Board.squareSize;
        ctx.fillRect(col*size, row*size, size, size);
    }
}