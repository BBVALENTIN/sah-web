import {MousePractice} from "../practice/mouse-practice.js";
import {Board} from "../tools/Board.js";
import {mvData, OptimisedMove} from "../tools/Types.js";
import {MoveList} from "../tools/MoveList.js";
import {SidesExplicit} from "../tools/Enums.js";

export class MouseBot extends MousePractice {
    private gameId: string;

    constructor(canvas: HTMLCanvasElement, board: Board, gameId: string, moveList: MoveList, playerSide: SidesExplicit) {
        super(canvas, board, moveList);
        this.gameId = gameId;
        this.currentColor = playerSide;

        this.endpoint = "/api/bot/move"; // maybe rename this endpoint so it's more intuitive (its the player's move)
    }

    protected afterMove(result: OptimisedMove): void {
    }


    async applyBotMove(mutare: string): Promise<void> {
        const fromCol = mutare.charCodeAt(0) - 97;
        const fromRow = 8 - parseInt(mutare.charAt(1));
        const targetCol = mutare.charCodeAt(2) - 97;
        const targetRow = 8 - parseInt(mutare.charAt(3));

        const moveData = {
            gameId: this.gameId,
            moveCoords: {fromRow, fromCol, targetRow, targetCol}
        };

        const response = await fetch('/api/bot/bot-move', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(moveData)
        });

        if (response.ok) {
            const result: OptimisedMove = await response.json();
            this.board.setPiecesFromFEN(result.fen);
            this.board.redraw();
            this.moveList.addMove(result.pgn);
            this.addMoveToCopyable(result.pgn);
            this.playSound(result);
            this.updateFEN(result.fen);
        }
    }

    protected buildRequestBody(moveData: mvData): any {
      return {
          gameId: this.gameId,
          moveCoords: moveData
      }
    }
}