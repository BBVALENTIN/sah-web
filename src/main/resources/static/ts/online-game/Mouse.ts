    import { Board } from "../tools/Board.js";
    import { SoundManager } from "../audio/soundManager.js";
    import { Piece } from "../pieces/Piece.js";
    import {lobbyInfo} from "../tools/Types.js";
    import {state, culoareCurenta} from "./WebSockets.js";
    import {loggedUsername, getInfoLobby} from "../misc/APIs.js";


    export class Mouse {
        canvas: HTMLCanvasElement;
        board: Board;
        selectedPiece: Piece | undefined;
        soundManager: SoundManager = new SoundManager();
        offsetX: number;
        offsetY: number;
        winner: 1 | 0 | -1 | undefined;
        lobbyInfo: lobbyInfo | undefined;
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

            let col: number = Math.floor(x / Board.squareSize);
            let row: number = Math.floor(y / Board.squareSize);

            if(this.board.isBlack) {
                col = 7 - col;
                row = 7 - row;
            }
            return { col, row, x, y};
        }

        public onMouseDown(e:any):void {
            const { col, row, x, y} = this.getSquareFromMouse(e);
            const piece = this.board.getPiece(row, col);
            if (piece && piece.color === culoareCurenta) {
                this.selectedPiece = piece;
                this.selectedPiece.isDragging = true;

                const visualCol = this.board.isBlack ? 7 - piece.col : piece.col;
                const visualRow = this.board.isBlack ? 7 - piece.row : piece.row;

                this.offsetX = x - visualCol * Board.squareSize;
                this.offsetY = y - visualRow * Board.squareSize;

                this.board.redraw(this.board.pieces.filter(p => p !== piece), this.selectedPiece);
            }
        }
        public async MouseMove(e:any):Promise<void> {
            const { col, row, x, y} = this.getSquareFromMouse(e);
            const piece = this.board.getPiece(row, col);
            if(piece && piece.color == culoareCurenta)
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
            const currentLobbyId = this.lobbyInfo?.lobbyId;
            if(!currentLobbyId) {
                console.error("Lobby not found.");
                return;
            }

            this.board.setLastMove(this.selectedPiece.row, this.selectedPiece.col, row, col);

            try {
                state.stompClient.send(
                    "/app/chess.move",
                    {},
                    JSON.stringify({
                        fromRow: this.selectedPiece!.row,
                        fromCol: this.selectedPiece!.col,
                        toRow: row,
                        toCol: col,
                        player: loggedUsername,
                        lobbyId: currentLobbyId
                    })
                );
            } catch(err){
                console.log("Error: ", err);
            } finally {
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
    }