import {Board} from "../tools/Board.js";
import {Piece} from "../pieces/Piece.js";
import {SoundManager} from "../audio/soundManager.js";
import {SidesExplicit, moveSounds} from "../tools/Enums.js";
import {Mutare_Reusita, OptimisedMove} from "../tools/Types.js";
import {MoveList} from "../tools/MoveList.js";

const fenOutput = document.getElementById('FEN') as HTMLInputElement;
const pgnOutput = document.getElementById('PGN') as HTMLTextAreaElement;
export let FEN: string = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'; // starting FEN
export class MousePractice {
    canvas: HTMLCanvasElement;
    board: Board;
    selectedPiece: Piece | undefined;
    soundManager: SoundManager = new SoundManager();
    offsetX: number;
    offsetY: number;
    moveNumber: number;
    currentColor: SidesExplicit;
    currentMove: string;
    moveList: MoveList;
    onEngineRequest?: (fen: string) => void;
    constructor(canvas: HTMLCanvasElement, board: Board, moveList: MoveList) {
        this.canvas = canvas;
        this.board = board;
        this.moveList = moveList;
        this.selectedPiece = undefined;
        this.offsetX = 0;
        this.offsetY = 0;
        this.moveNumber = 1;
        this.currentColor = SidesExplicit.WHITE;
        this.currentMove = "";
        this.canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        this.canvas.addEventListener("mousemove", this.MouseMove.bind(this));
        this.canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    async handleMutareAPI(e: any):Promise<OptimisedMove> {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        let mutare: Promise<OptimisedMove>;
        let errorText: any;
        const moveData = {
            fromRow: this.selectedPiece!.row,
            fromCol: this.selectedPiece!.col,
            targetRow: row,
            targetCol: col
        };
        console.log(moveData);
        const res: Response = await fetch(`/api/chess/omove`,
            {
                method: "POST",
                headers: {
                    'Content-type': 'application/json'
                },
                body: JSON.stringify(moveData)
            });


        if(!res.ok) {
            const errorText = await res.text();
            this.reset();
            throw new Error(errorText);
        }
        mutare = await res.json();
        console.log(mutare);
        return mutare;
    }

    getSquareFromMouse(e: any){
        const rect: DOMRect = this.canvas.getBoundingClientRect();
        const x: number = e.clientX - rect.left;
        const y: number = e.clientY - rect.top;

        let col: number = Math.floor(x / Board.squareSize);
        let row: number = Math.floor(y / Board.squareSize);

        if(this.board.isBlack) {
            col = 7 - col;
            row = 7 - row;
        }
        return { col, row, x, y};
    }



    public async onMouseDown(e:any) {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piesa = this.board.getPiece(row, col);
        if (piesa && piesa.color === this.currentColor) {
            this.selectedPiece = piesa;
            this.selectedPiece.isDragging = true;

            const vizualCol = this.board.isBlack ? 7 - piesa.col : piesa.col;
            const vizualRow = this.board.isBlack ? 7 - piesa.row : piesa.row;

            this.offsetX = x - vizualCol * Board.squareSize;
            this.offsetY = y - vizualRow * Board.squareSize;

            this.board.redraw(this.board.pieces.filter(p => p !== piesa), this.selectedPiece);
        }
    }
    public async MouseMove(e:any):Promise<void> {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piesa = this.board.getPiece(row, col);
        if(piesa && piesa.color == this.currentColor)
            this.canvas.style.cursor = "grab";
        else
            this.canvas.style.cursor = "default";
        if (!this.selectedPiece) return;

        this.canvas.style.cursor = "grabbing";
        this.selectedPiece.dragX = x - this.offsetX;
        this.selectedPiece.dragY = y - this.offsetY;

        this.board.redraw(this.board.pieces, this.selectedPiece);
    }
    public async onMouseUp(e: any): Promise<void> {
        if(!this.selectedPiece) { return; }
        const { col, row } = this.getSquareFromMouse(e);

        this.board.setLastMove(this.selectedPiece.row, this.selectedPiece.col, row, col);

        try {
            const result:OptimisedMove | undefined = await this.handleMutareAPI(e);
            console.log(result);
            if(result === undefined)
                return;

            this.playSound(result);

            this.moveList.addMove(result.pgn);
            this.board.setPiecesFromFEN(result.fen); // function to set pieces from FEN
            this.afterMove(result);
            FEN = result.fen;
            if(this.onEngineRequest)
                this.onEngineRequest(FEN);
            fenOutput.value = FEN;
            this.addMoveToCopyable(result.pgn);
        } catch(err){
            console.error("Error regarding the API: ", err);
        } finally {
            this.reset();
        }
    }

    reset() {
        if(this.selectedPiece) {
            this.selectedPiece.isDragging = false;
            this.selectedPiece.dragX = undefined;
            this.selectedPiece.dragY = undefined;
        }

        this.selectedPiece = undefined;

        this.canvas.style.cursor = "default";
        this.board.redraw(this.board.pieces);
    }

    resetGame() {
        this.reset();
        this.moveNumber = 1;
        this.currentColor = SidesExplicit.WHITE;
    }

    addMoveToCopyable(currentPGN: string) {
        this.moveNumber += 1;
        if(this.moveNumber % 2 == 0)
            this.currentMove = (this.moveNumber / 2).toString() + ". " + currentPGN;
        else
            this.currentMove = " " + currentPGN + " ";

        pgnOutput.value += this.currentMove;
    }

    playSound(result: OptimisedMove): void {
        if(result.isCheckMate){
            this.soundManager.play("checkmate");
            this.soundManager.play("end")
        }
        else if(result.isCheck) { this.soundManager.play("check");}
        else if(result.pgn.includes('x')) {this.soundManager.play("capture");}
        else {this.soundManager.play("move"); }
    }

    protected afterMove(result: OptimisedMove): void {
        this.currentColor = result.currentColor;
    }

    protected updateFEN(fen: string): void {
        FEN = fen;
        const fenOutput = document.getElementById('FEN') as HTMLInputElement;
        if(fenOutput) fenOutput.value = fen;
    }
}