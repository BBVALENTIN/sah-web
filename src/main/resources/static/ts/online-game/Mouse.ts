    import { Board } from "../tools/Board.js";
    import { SoundManager } from "../audio/soundManager.js";
    import { Piece } from "../pieces/Piece.js";
    import {lobbyInfo} from "../tools/Types.js";
    import {state, culoareCurenta} from "./WebSockets.js";
    import {loggedUsername, getInfoLobby} from "../misc/APIs.js";


    export class Mouse {
        canvas: HTMLCanvasElement;
        board: Board;
        piesaSelectata: Piece | undefined;
        soundManager: SoundManager = new SoundManager();
        offsetX: number;
        offsetY: number;
        winner: 1 | 0 | -1 | undefined;
        lobbyInfo: lobbyInfo | undefined;
        constructor(canvas: HTMLCanvasElement, board: Board) {
            this.canvas = canvas;
            this.board = board;
            this.piesaSelectata = undefined;
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
            const piesa = this.board.getPiece(row, col);
            if (piesa && piesa.color === culoareCurenta) {
                this.piesaSelectata = piesa;
                this.piesaSelectata.isDragging = true;

                const vizualCol = this.board.isBlack ? 7 - piesa.col : piesa.col;
                const vizualRow = this.board.isBlack ? 7 - piesa.row : piesa.row;

                this.offsetX = x - vizualCol * Board.squareSize;
                this.offsetY = y - vizualRow * Board.squareSize;

                this.board.redraw(this.board.pieces.filter(p => p !== piesa));
            }
        }
        public async MouseMove(e:any):Promise<void> {
            const { col, row, x, y} = this.getSquareFromMouse(e);
            const piesa = this.board.getPiece(row, col);
            if(piesa && piesa.color == culoareCurenta)
                this.canvas.style.cursor = "grab";
            else
                this.canvas.style.cursor = "default";
            if (!this.piesaSelectata) return;

            this.canvas.style.cursor = "grabbing";
            this.piesaSelectata.dragX = x - this.offsetX;
            this.piesaSelectata.dragY = y - this.offsetY;

            this.board.redraw(this.board.pieces, this.piesaSelectata);
        }
        public async onMouseUp(e: any): Promise<void> {
            if(!this.piesaSelectata) { return; }
            const { col, row } = this.getSquareFromMouse(e);
            const currentLobbyId = this.lobbyInfo?.lobbyId;
            if(!currentLobbyId) {
                console.error("nu stim lobbyId");
                return;
            }

            this.board.setLastMove(this.piesaSelectata.row, this.piesaSelectata.col, row, col);

            try {
                state.stompClient.send(
                    "/app/chess.move",
                    {},
                    JSON.stringify({
                        fromRow: this.piesaSelectata!.row,
                        fromCol: this.piesaSelectata!.col,
                        toRow: row,
                        toCol: col,
                        player: loggedUsername,
                        lobbyId: currentLobbyId
                    })
                );
            } catch(err){
                console.log("eroare cine stie de ce");
            } finally {
                if(this.piesaSelectata) {
                    this.board.redraw(this.board.pieces);
                    this.piesaSelectata.isDragging = false;
                    this.piesaSelectata.dragX = undefined;
                    this.piesaSelectata.dragY = undefined;
                    this.piesaSelectata = undefined;
                    this.canvas.style.cursor = "default";
                }
            }
        }
    }