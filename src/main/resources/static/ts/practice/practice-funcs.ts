import {minimalState, PieceDTO} from "../tools/Types.js";
import {FEN, MousePractice} from "./MousePractice.js";
import {initCanvas, initBoard, onButtonClick} from "../tools/chess-utils.js";

type stockfishLine = {
    rank: number;
    evaluation: string;
    moves: string[];
}

let stockfishOutputs: stockfishLine[] = [];
const stockfishTrigger = document.getElementById('stockfish-trigger') as HTMLInputElement;
export let engineOn: boolean = false;

export const { board, moveList, canvas } = initCanvas('chessCanvas', 'move-list');
await initBoard(board);
await loadBoard();
const mousePractice = new MousePractice(canvas, board, moveList);
mousePractice.onEngineRequest = (fen: string) => {
    if(engineOn) requestStockfishMove(fen);
};

const stockfish = new Worker('/libs/stockfish/stockfish.js');
stockfishTrigger.addEventListener('change', () => {
    if(stockfishTrigger.checked) {
        engineOn = true;
        requestStockfishMove(FEN);
    }
    else {
        engineOn = false;
        uciCmd('stop');
    }
});

stockfish.onmessage = function(event) {
    const line = event.data;

    if (line.includes('multipv')) {
        const parts = line.split(" ");

        const multipvIndex = parts.indexOf("multipv");
        const scoreIndex = parts.indexOf("score");
        const pvIndex = parts.indexOf("pv");

        const rank = parseInt(parts[multipvIndex + 1]);
        const isBlackToMove = FEN.split(' ')[1] === 'b';

        stockfishOutputs[rank - 1] = {
            rank: rank,
            evaluation: formatEvaluation(parts[scoreIndex + 1], parts[scoreIndex + 2], isBlackToMove),
            moves: parts.slice(pvIndex + 1)
        };

        renderStockfishLines(stockfishOutputs.filter(Boolean)); // cleaning empty arrays
    }
};

export function requestStockfishMove(fenCurent: string) {
    uciCmd(`position fen ${fenCurent}`);
    uciCmd("setoption name MultiPV value 3");
    uciCmd('go movetime 8000'); // wait 8 seconds
}

async function loadBoard() {
    try {
        const response = await fetch('/practiceBoard');
        if(response.ok) {
            const minimalState: minimalState = await response.json();
            const piecesData = minimalState.Pieces;
            if(minimalState.currentPGN != null )
                moveList.addWholePGN(minimalState.currentPGN);
            board.setPiecesFromServer(piecesData);
            board.redraw();
        }
    }
    catch(e) {
        console.error('Error: ', e);
    }
}

function handleFlip() {
    board.getOrientation() ? board.setOrientation(false) : board.setOrientation(true);
    board.redraw(board.pieces);
}

async function handleReset() {
    const reset = await fetch('/api/chess/reset');
    if(reset.ok) {
        const newPieces: PieceDTO[] = await reset.json(); // Delete this after finishing the page
        board.setPiecesFromServer(newPieces);
        board.redraw();
        moveList.reset();
        mousePractice.resetGame();
    }
    else
        console.log("Fail");
}

function formatEvaluation(type: string, value: string, isBlackToMove: boolean): string {
    if(type === 'cp')
    {
        let score = parseInt(value) / 100;

        if (isBlackToMove) score = -score;

        return score > 0 ? `+${score.toFixed(2)}` : `${score.toFixed(2)}`;
    }

    if(type === 'mate') {
        return `M${value}`;
    }

    return '0.00';
}

function uciCmd(msg: string) {
    stockfish.postMessage(msg);
}

function renderStockfishLines(lines: stockfishLine[]) {
    const container = document.getElementById('stockfish-lines');

    if(!container) return;
    container.innerHTML = "";

    lines.forEach(line => {

        const lineElement = document.createElement("div");
        lineElement.className = "engine-line";

        lineElement.innerHTML = `
         <span class="evaluation">${line.evaluation}</span>
         <span class="moves">${line.moves.join(" ")}</span>
     `;
        container.appendChild(lineElement);
    });
}

export async function initPractice() {
    await initBoard(board);
    await loadBoard();
    onButtonClick('flipBoard', handleFlip);
    onButtonClick('resetBoard', handleReset);
    uciCmd('uci');
    uciCmd('isready');
}