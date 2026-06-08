import {minimalState, PiesaDTO} from "../tools/Types.js";
import {Tabla} from "../tools/Tabla.js";
import {FEN, MousePractice} from "./MousePractice.js";
import {initCanvas, initTabla, onButtonClick} from "../tools/initApp.js";

type stockfishLine = {
    rank: number;
    evaluation: string;
    moves: string[];
}

let stockfishOutputs: stockfishLine[] = [];
const stockfishTrigger = document.getElementById('stockfish-trigger') as HTMLInputElement;
export let engineOn: boolean = false;

export const { tabla, moveList, canvas } = initCanvas('chessCanvas', 'move-list');
await initTabla(tabla);
await loadBoard();
const mousePractice = new MousePractice(canvas, tabla, moveList);
mousePractice.onEngineRequest = (fen: string) => {
    if(engineOn) cereMutareDeLaStockfish(fen);
};
onButtonClick('flipBoard', handleFlip);
onButtonClick('resetBoard', handleReset);

const stockfish = new Worker('/libs/stockfish/stockfish.js');
stockfishTrigger.addEventListener('change', () => {
    if(stockfishTrigger.checked) {
        engineOn = true;
        cereMutareDeLaStockfish(FEN);
    }
    else {
        engineOn = false;
        stockfish.postMessage('stop');
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

export function cereMutareDeLaStockfish(fenCurent: string) {
    stockfish.postMessage(`position fen ${fenCurent}`);
    stockfish.postMessage("setoption name MultiPV value 3");
    stockfish.postMessage('go movetime 8000'); // wait 8 seconds
}

// to implement
function applyMove(mutare: string) {

    const fromCol = mutare.charCodeAt(0) - 97;
    const fromRow = 8 - parseInt(mutare.charAt(1));

    const targetCol = mutare.charCodeAt(2) - 97;
    const targetRow = 8 - parseInt(mutare.charAt(3));
}

stockfish.postMessage('uci');
stockfish.postMessage('isready');

async function loadBoard() {
    try {
        const response = await fetch('/practiceBoard');
        if(response.ok) {
            const minimalState: minimalState = await response.json();
            const piecesData = minimalState.Piese;
            if(minimalState.currentPGN != null )
                moveList.addWholePGN(minimalState.currentPGN);
            tabla.setPiecesFromServer(piecesData);
            tabla.redesenare();
        }
    }
    catch(e) {
        console.error('Eroare: ', e);
    }
}

loadBoard();

function handleFlip() {
    tabla.getOrientare() ? tabla.setOrientare(false) : tabla.setOrientare(true);
    tabla.redesenare(tabla.piese);
}

async function handleReset() {
    const reset = await fetch('/api/chess/reset');
    if(reset.ok) {
        console.log("Board reseted successfully");
        const pieseleLivrateNoi: PiesaDTO[] = await reset.json(); // Delete this after finishing the page
        console.log(pieseleLivrateNoi);
        tabla.setPiecesFromServer(pieseleLivrateNoi);
        tabla.redesenare();
        mousePractice.resetByButton(); // might overhaul this, looks weird to be here
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