import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "./Enums.js";

export class Board {
    private canvas: HTMLCanvasElement;
    private ctx: CanvasRenderingContext2D;
    private static readonly BOARD_SIZE  = 8;
    private readonly colorBlackSquares: string = "#6F8FAF";
    private readonly colorWhiteSquares: string = "#DCE1E6";
    private squareSize: number = 75;
    lastMove?: {
        fromRow: number,
        fromCol: number,
        toRow: number,
        toCol: number
    };
    pieces: Piece[];
    imageCache: Record<string, HTMLImageElement>
    isBlack: boolean;

    setLastMove(fromRow: number, fromCol: number, toRow: number, toCol: number) {
        this.lastMove = { fromRow, fromCol, toRow, toCol };
    }

    constructor(canvasId: string) {
        this.canvas = document.getElementById(canvasId) as HTMLCanvasElement;

        const ctx = this.canvas.getContext("2d");

        if(!ctx) throw new Error("Cannot create canvas context");

        this.ctx = ctx;

        this.pieces = []; // maybe redundant
        this.imageCache = {};
        this.isBlack = false;

        this.resize();

        window.addEventListener('resize', () => {
            this.resize();
            this.redraw();
        });
    }

    // need a resize function for the whole page, no tjust the canvas
    private resize() {
        // rule of thumb, always name the element chess-playground and have a parent div on it
        const playground = document.querySelector('.chess-playground') as HTMLElement;
        const parentElement = playground.parentNode as HTMLDivElement;
        console.log("viewport:", window.innerWidth, window.innerHeight);

        console.log(
            "parent:",
            parent.innerHeight,
            parent.innerWidth
        );

        console.log(
            "document:",
            document.documentElement.clientWidth,
            document.documentElement.clientHeight
        );

        console.log("parent rect:", parentElement.getBoundingClientRect());
        console.log("canvas rect:", this.canvas.getBoundingClientRect());
        const rect = parentElement.getBoundingClientRect();
        let availableHeight = window.innerHeight - rect.top;
        console.log(parentElement.children.length);
        if(parentElement.children.length < 2)
            availableHeight *= 0.8;

        const boardSquareSize = Math.min(parentElement.clientWidth, availableHeight);
        this.canvas.width = boardSquareSize;
        this.canvas.height = boardSquareSize;

        this.squareSize = boardSquareSize / 8;
    }

    public getCanvas(): HTMLCanvasElement {
        return this.canvas;
    }
    public getPiece(row: number, col: number): Piece  | undefined {
        return this.pieces.find(p => p.row === row && p.col === col);
    }

    public setOrientation(isBlack: boolean) {
        this.isBlack = isBlack;
    }

    public getOrientation() {
        return this.isBlack;
    }

    public getSquareSize():number {
        return this.squareSize
    }

    public async loadImages(): Promise<void> {
        const tipuri: PieceType[] = Object.values(PieceType);
        const culori: string[] = [ "white", "black"];
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
                        console.error(`We couldn't load the images: ${img.src}`);
                        resolve();
                    };
                });
                promises.push(p);
            });
        });

        await Promise.all(promises);
    }

    public redraw(pieces: Piece[] = this.pieces, selectedPiece?: Piece): void {
        this.drawBoard();
        this.drawLastMove();
        this.drawPieces(pieces, selectedPiece);
        this.drawCoordinates();
    }

    private drawBoard(): void {
        let c = 0;
        const size = this.squareSize;

        for(let row = 0; row < Board.BOARD_SIZE; row++){
            for(let col = 0; col < Board.BOARD_SIZE; col++)
                {
                    this.ctx.fillStyle = c === 0 ? this.colorWhiteSquares : this.colorBlackSquares;
                    c = 1 - c;
                    this.ctx.fillRect(col * size, row * size, size, size);
                }
            c = 1 - c;
        }
    }

    private drawPieces(pieces: Piece[], selectedPiece?: Piece): void {
        pieces.forEach(p => this.drawOrientedPiece(p));

        if(selectedPiece && selectedPiece.img?.complete) {
            this.drawOrientedPiece(selectedPiece);
        }
    }

    private drawOrientedPiece(piece: Piece): void {
        const size = this.squareSize;

        if (piece.isDragging && piece.dragX !== undefined && piece.dragY !== undefined) {
            piece.draw(this, this.ctx);
            return;
        }

        const visualCol = this.isBlack ? 7 - piece.col : piece.col;
        const visualRow = this.isBlack ? 7 - piece.row : piece.row;

        if(piece.img?.complete) {
            this.ctx.drawImage(
                piece.img,
                visualCol * size,
                visualRow * size,
                size,
                size
            );
        }
    }

    private drawCoordinates() {
        const ctx = this.ctx;
        const size = this.squareSize;
        ctx.font = "600 14px Fira Code, sans-serif";
        ctx.fillStyle = "#4a4a4a";

        for (let i = 0; i < 8; i++) {
            const letter = String.fromCharCode(97 + (this.isBlack ? 7 - i : i));
            const number = this.isBlack ? i + 1 : 8 - i;

            ctx.fillText(letter, i * size + size - 14, 8 * size - 6);
            ctx.fillText(number.toString(), 4, i * size + 14);
        }
    }

    private createPieceFromData(data: PieceData): Piece
    {
        const { type, color, row, col } = data;

        const piece: Piece = new Piece(
            type,
            color,
            row,
            col
        );

        const colorKey = (color === SidesExplicit.WHITE) ? "white" : "black";
        const imgKey = `${colorKey}-${type.toLowerCase()}`;

        if (this.imageCache[imgKey]) {
            piece.img = this.imageCache[imgKey];
        }

        return piece;
    }

    public setPiecesFromFEN(FEN: string) {
        const piecePlacement = FEN.split(' ')[0];
        const ranks = piecePlacement.split('/');

        const pieceTypeMap: Record<string, PieceType> = {
            p: PieceType.PAWN,
            n: PieceType.KNIGHT,
            b: PieceType.BISHOP,
            r: PieceType.ROOK,
            q: PieceType.QUEEN,
            k: PieceType.KING
        }

        const newPieces: Piece[] = [];

        ranks.forEach((rank, rowIndex) => {
           let colIndex = 0;

           for(const char of rank) {
               if(/\d/.test(char)) {
                   colIndex += parseInt(char, 10);
               }
               else {
                   const isWhite = char === char.toUpperCase();
                   const type = pieceTypeMap[char.toLowerCase()];

                   if(!type) {
                       console.error(`Unknown character in FEN: ${char}`);
                       colIndex++;
                       continue;
                   }

                   const color = isWhite ? SidesExplicit.WHITE : SidesExplicit.BLACK;

                   const piece = this.createPieceFromData({
                       type,
                       color,
                       row: rowIndex,
                       col: colIndex
                   });

                   newPieces.push(piece);
                   colIndex++;
               }
           }
        });

        this.pieces = newPieces;
    }


    private drawLastMove() {
        const size = this.squareSize;
        if(!this.lastMove) return;

        let {fromRow, fromCol, toRow, toCol} = this.lastMove;

        if(this.isBlack) {
            fromRow = 7 - fromRow;
            fromCol = 7 - fromCol;
            toRow = 7 - toRow;
            toCol = 7 - toCol;
        }

        this.drawHighlight(fromRow, fromCol);
        this.drawHighlight(toRow, toCol);
    }

    private drawHighlight(row: number, col: number) {
        this.ctx.fillStyle = 'rgba(255, 193, 7, 0.30)';
        const size = this.squareSize;
        this.ctx.fillRect(col*size, row*size, size, size);
    }
}

interface PieceData {
    type: PieceType,
    color: SidesExplicit,
    row: number,
    col: number;
}