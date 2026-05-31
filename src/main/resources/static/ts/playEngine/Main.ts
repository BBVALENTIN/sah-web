import {initCanvas, initTabla} from "../tools/initApp.js";
import {culoriPiesa, SidesExplicit} from "../tools/Enums.js";
import {MouseBot} from "./MouseBot.js";
import {minimalState} from "../tools/Types.js";

export const { tabla, moveList, canvas } = initCanvas('chessCanvas', 'move-list');
await initTabla(tabla);
await loadBoard();

const startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
let botGameId: string;
let mouseBot: MouseBot;

const stockfish = new Worker('/libs/stockfish/stockfish.js');

function uciCmd(cmd: string) {
    stockfish.postMessage(cmd);
}

uciCmd('uci');
uciCmd('isready');

function requestStockfishMove(fen: string) {
    uciCmd(`position fen ${fen}`);
    uciCmd('go movetime 2000');
}

stockfish.onmessage = async function(event) {
    const line = event.data;
    if (line.startsWith('bestmove')) {
        const bestMove = line.split(' ')[1];
        if (bestMove && bestMove !== '(none)') {
            await mouseBot.aplicaMutareBot(bestMove);
        }
    }
}

async function startBotGame(botSide: SidesExplicit) {
    document.getElementById('side-select-overlay')?.remove();

    const response = await fetch(`/api/bot/start?botSide=${botSide}`, { method: 'POST' });
    const data = await response.json();
    botGameId = data.gameId;

    mouseBot = new MouseBot(
        canvas, tabla, botGameId, moveList,
        botSide === SidesExplicit.BLACK ? culoriPiesa.ALB : culoriPiesa.NEGRU
    );

    mouseBot.onEngineRequest = (fen: string) => {
        requestStockfishMove(fen);
    };

    tabla.setOrientare(botSide === SidesExplicit.WHITE);

    if (botSide === SidesExplicit.WHITE) {
        requestStockfishMove(startingFEN);
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

document.getElementById('playAsWhite')?.addEventListener('click', () => startBotGame(SidesExplicit.BLACK));
document.getElementById('playAsBlack')?.addEventListener('click', () => startBotGame(SidesExplicit.WHITE));