import { Board } from "../tools/Board.js";
import { SoundManager } from "../audio/soundManager.js";
import { Piece } from "../tools/Piece.js";
import {lobbyInfo} from "../tools/Types.js";
import {state, currentColor} from "./WebSockets.js";
import {PromotionManager} from "../tools/PromotionManager.js";
import {getInfoLobby} from "./onlineg-funcs.js";

export class Mouse {
    canvas: HTMLCanvasElement;
    board: Board;
    selectedPiece: Piece | undefined;
    soundManager: SoundManager = new SoundManager();
    offsetX: number;
    offsetY: number;
    winner: 1 | 0 | -1 | undefined;
    lobbyInfo: lobbyInfo | undefined;
    private promotionManager: PromotionManager = new PromotionManager();

    constructor(canvas: HTMLCanvasElement, board: Board) {
        this.canvas = canvas;
        this.board = board;
        this.selectedPiece = undefined;
        this.offsetX = 0;
        this.offsetY = 0;
        this.winner = undefined;
        this.initLobby();
        this.canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        this.canvas.addEventListener("mousemove", this.MouseMove.bind(this));
        this.canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    private async initLobby() {
        this.lobbyInfo = await getInfoLobby();
    }

    getSquareFromMouse(e: any){
        const rect: DOMRect = this.canvas.getBoundingClientRect();
        const x: number = e.clientX - rect.left;
        const y: number = e.clientY - rect.top;

        let col: number = Math.floor(x / this.board.getSquareSize());
        let row: number = Math.floor(y / this.board.getSquareSize());

        if(this.board.isBlack) {
            col = 7 - col;
            row = 7 - row;
        }
        return { col, row, x, y};
    }

    public onMouseDown(e:any):void {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piece = this.board.getPiece(row, col);
        if (piece && piece.color === currentColor) {
            this.selectedPiece = piece;
            this.selectedPiece.isDragging = true;

            const visualCol = this.board.isBlack ? 7 - piece.col : piece.col;
            const visualRow = this.board.isBlack ? 7 - piece.row : piece.row;

            this.offsetX = x - visualCol * this.board.getSquareSize();
            this.offsetY = y - visualRow * this.board.getSquareSize();

            this.board.redraw(this.board.pieces.filter(p => p !== piece), this.selectedPiece);
        }
    }

    public async MouseMove(e:any):Promise<void> {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piece = this.board.getPiece(row, col);
        if(piece && piece.color == currentColor)
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
        const { col, row, x, y } = this.getSquareFromMouse(e);
        const currentLobbyId = this.lobbyInfo?.lobbyId;
        if(!currentLobbyId) {
            console.error("Lobby not found.");
            return;
        }

        this.board.setLastMove(this.selectedPiece.row, this.selectedPiece.col, row, col);
        const fromRow = this.selectedPiece!.row;
        const fromCol = this.selectedPiece!.col;
        const targetRow = row;
        const targetCol = col;

        if(this.promotionManager.isPromotionMove(this.selectedPiece, this.selectedPiece.row, targetRow)) {

            this.promotionManager.setPendingMove({
               fromRow: fromRow,
               fromCol: fromCol,
               targetRow: targetRow,
               targetCol: targetCol
            });

            this.promotionManager.showPromotionModal(this.selectedPiece.color, {x: e.clientX, y: e.clientY}, async (promotionPiece: string) => {
                // i know this is bad, will make it type safe, but promotionPiece should have only this name momentarily
                try {

                    if(!state.stompClient || !state.connected) {
                        console.error("STOMP is not connected");
                        return;
                    }

                    state.stompClient.publish({
                        destination: "/app/chess.move",
                        body: JSON.stringify({
                            moveCoords: {fromRow, fromCol, targetRow, targetCol, promotionPiece},
                            lobbyId: currentLobbyId
                        })
                    });
                } catch (err) {
                    console.error("Error regarding ws move (promotion)", err);
                }
                finally {
                    this.reset();
                }
            });
            return;
        }


        try {
            if(!state.stompClient || !state.connected) {
                console.error("STOMP is not connected");
                return;
            }

            state.stompClient.publish({
                destination: "/app/chess.move",
                body: JSON.stringify({
                    moveCoords: {fromRow, fromCol, targetRow, targetCol},
                    lobbyId: currentLobbyId
                })
            });
        } catch(err){
            console.log("Error: ", err);
        } finally {
            this.reset();
        }
    }

    private reset() {
        if(this.selectedPiece) {
            this.board.redraw(this.board.pieces);
            this.selectedPiece.isDragging = false;
            this.selectedPiece.dragX = undefined;
            this.selectedPiece.dragY = undefined;
            this.selectedPiece = undefined;
            this.canvas.style.cursor = "default";
        }
    }
}