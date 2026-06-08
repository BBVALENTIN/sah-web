import {MousePractice} from "../practice/MousePractice.js";
import {Board} from "../tools/Board";
import {Mutare_Reusita} from "../tools/Types.js";
import {MoveList} from "../tools/MoveList.js";
import {SidesExplicit} from "../tools/Enums.js";

export class MouseBot extends MousePractice {
    private gameId: string;

    constructor(canvas: HTMLCanvasElement, tabla: Board, gameId: string, moveList: MoveList, playerSide: SidesExplicit) {
        super(canvas, tabla, moveList);
        this.gameId = gameId;
        this.currentColor = playerSide;
    }

    protected afterMove(result: Mutare_Reusita): void {
    }

    async handleMutareAPI(e: any): Promise<Mutare_Reusita> {
        const { col, row } = this.getSquareFromMouse(e);
        const moveData = {
            gameId: this.gameId,
            fromRow: this.piesaSelectata!.row,
            fromCol: this.piesaSelectata!.col,
            toRow: row,
            toCol: col
        };

        const respMutare = await fetch('/api/bot/move', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(moveData)
        });

        if (!respMutare.ok) {
            const errorText = await respMutare.text();
            this.reset();
            throw new Error(errorText);
        }

        return respMutare.json();
    }

    async aplicaMutareBot(mutare: string): Promise<void> {
        const fromCol = mutare.charCodeAt(0) - 97;
        const fromRow = 8 - parseInt(mutare.charAt(1));
        const targetCol = mutare.charCodeAt(2) - 97;
        const targetRow = 8 - parseInt(mutare.charAt(3));

        const moveData = {
            gameId: this.gameId,
            fromRow,
            fromCol,
            toRow: targetRow,
            toCol: targetCol
        };

        const response = await fetch('/api/bot/bot-move', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(moveData)
        });

        if (response.ok) {
            const result: Mutare_Reusita = await response.json();
            this.tabla.setPiecesFromServer(result.updatedPieces);
            this.tabla.redraw();
            this.moveList.addMove(result.pgn);
            this.addMoveToCopyable(result.pgn);
            this.playSound(result);
            this.updateFEN(result.fen);
        }
    }
}