import {minimalState, PiesaDTO} from "../tools/Types.js";
import {Tabla} from "../Tabla.js";
import {FEN, MousePractice} from "./MousePractice.js";
import {initCanvas, initTabla, onButtonClick} from "../tools/initApp.js";

type stockfishLine = {
    rank: number;
    evaluation: string;
    moves: string[];
}

let stockfishOutputs: stockfishLine[] = [];
let loggedUsername: string;
const stockfishTrigger = document.getElementById('stockfish-trigger') as HTMLInputElement;
export let engineOn: boolean = false;
const canvas = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx: CanvasRenderingContext2D = canvas.getContext('2d')!;
export const tabla = new Tabla(ctx);
await tabla.loadImages();
export const moveList:MoveList = new MoveList("move-list");
const mousePractice = new MousePractice(canvas, tabla);

const size: number = Tabla.squareSize * 8;
canvas.width = size;
canvas.height = size;

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
        stockfishOutputs[rank - 1] = {
            rank: rank,
            evaluation: formatEvaluation(parts[scoreIndex + 1], parts[scoreIndex + 2]),
            moves: parts.slice(pvIndex + 1)
        };

        renderStockfishLines(stockfishOutputs.filter(Boolean)); // cleaning empty arrays
    }
};

export function cereMutareDeLaStockfish(fenCurent: string) {
    stockfish.postMessage(`position fen ${fenCurent}`);
    stockfish.postMessage("setoption name MultiPV value 3");
    stockfish.postMessage('go depth 18');
}

// to implement
function aplicaMutareBot(mutare: string) {

    const fromCol = mutare.charCodeAt(0) - 97;          // 'e' devine 4
    const fromRow = 8 - parseInt(mutare.charAt(1));     // '7' devine 1

    const targetCol = mutare.charCodeAt(2) - 97;        // 'e' devine 4
    const targetRow = 8 - parseInt(mutare.charAt(3));   // '5' devine 3

    console.log(`Tradus pentru Java: faMiscare(${fromRow}, ${fromCol}, ${targetRow}, ${targetCol})`);

}

stockfish.postMessage('uci');
stockfish.postMessage('isready');

async function getUserInfo() {
    try {
        const response = await fetch('/info/user'); // will need this
        if (response.ok) {
            const data = await response.json();
            loggedUsername = data.username;
        }
    } catch (e) {
        console.log(e);
    }
}

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

const flipboard = document.getElementById('flipBoard') as HTMLButtonElement;
const resetBoard = document.getElementById('resetBoard') as HTMLButtonElement;
flipboard.addEventListener('click', () => {
    console.log("clicked");
    tabla.getOrientare() ? tabla.setOrientare(false) : tabla.setOrientare(true);
    tabla.redesenare(tabla.piese);
});

resetBoard.addEventListener('click', async () => {
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
});

function formatEvaluation(type: string, value: string): string {
    if(type === 'cp')
    {
        const score = (parseInt(value)/100).toFixed(2);

        return parseFloat(score) > 0 ? `+${score}` : `${score}`;
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