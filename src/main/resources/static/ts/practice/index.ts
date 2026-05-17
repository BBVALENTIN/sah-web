import {minimalState} from "../Types.js";
import {Tabla} from "../Tabla.js";
import {MoveList} from "../MoveList.js";
import {MousePractice, FEN} from "./MousePractice.js";


let loggedUsername: string;
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

stockfish.onmessage = function(event) {
    const line = event.data;

    if (line.includes('multipv')) {
        const parts = line.split(" ");

        const multipvIndex = parts.indexOf("multipv");
        const scoreIndex = parts.indexOf("score");
        const pvIndex = parts.indexOf("pv");

        const rank = parts[multipvIndex + 1];

        const scoreType = parts[scoreIndex + 1];
        const scoreValue = parts[scoreIndex + 2];

        const moves = parts.slice(pvIndex + 1);
        console.log(rank, scoreType, scoreValue, moves);
    }
};

export function cereMutareDeLaStockfish(fenCurent: string) {
    stockfish.postMessage(`position fen ${fenCurent}`);
    stockfish.postMessage("setoption name MultiPV value 3");
    stockfish.postMessage('go depth 18');
}

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

flipboard.addEventListener('click', () => {
    console.log("clicked");
    tabla.getOrientare() ? tabla.setOrientare(false) : tabla.setOrientare(true);
    tabla.redesenare(tabla.piese);
});