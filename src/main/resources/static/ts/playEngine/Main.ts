import {initCanvas, initBoard, onButtonClick} from "../tools/initApp.js";
import {Sides, SidesExplicit} from "../tools/Enums.js";
import {MouseBot} from "./MouseBot.js";
import {minimalState} from "../tools/Types.js";

export const { board, moveList, canvas } = initCanvas('chessCanvas', 'move-list');

document.addEventListener('DOMContentLoaded', async () => {
    await initBoard(board);
});

const startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
let botGameId: string;
let mouseBot: MouseBot;

const stockfish = new Worker('/libs/stockfish/stockfish.js');
function uciCmd(cmd: string) { stockfish.postMessage(cmd); }
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
        if (bestMove && bestMove !== '(none)')
            await mouseBot.aplicaMutareBot(bestMove);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const savedGameId = sessionStorage.getItem('botGameId');
    if (savedGameId) {
        botGameId = savedGameId;
        document.getElementById('side-select-overlay')?.remove();
        await resumeGame(botGameId);
    } else {
        document.getElementById('side-select-overlay')?.style.setProperty('display', 'flex');
    }

    async function resumeGame(gameId: string) {
        try {
            const response = await fetch(`/api/bot/state/${gameId}`);
            if (!response.ok) {
                sessionStorage.removeItem('botGameId');
                document.getElementById('side-select-overlay')?.style.setProperty('display', 'flex');
                return;
            }
            const state = await response.json();
            if (state.currentPGN != null)
                moveList.addWholePGN(state.currentPGN);
            board.setPiecesFromServer(state.pieces);
            board.redraw();

            const botSide = state.botSide as SidesExplicit;
            mouseBot = new MouseBot(
                canvas, board, gameId, moveList,
                botSide === SidesExplicit.BLACK ? SidesExplicit.WHITE : SidesExplicit.BLACK
            );
            mouseBot.onEngineRequest = (fen: string) => requestStockfishMove(fen);
            board.setOrientation(botSide === SidesExplicit.WHITE);
        } catch(e) {
            console.error('Eroare resume:', e);
        }
    }

    async function startBotGame(botSide: SidesExplicit) {
        document.getElementById('side-select-overlay')?.remove();

        const response = await fetch(`/api/bot/start?botSide=${botSide}`, { method: 'POST' });
        const data = await response.json();
        botGameId = data.gameId;
        sessionStorage.setItem('botGameId', botGameId);

        mouseBot = new MouseBot(
            canvas, board, botGameId, moveList,
            botSide === SidesExplicit.BLACK ? SidesExplicit.WHITE : SidesExplicit.BLACK
        );
        mouseBot.onEngineRequest = (fen: string) => requestStockfishMove(fen);
        board.setOrientation(botSide === SidesExplicit.WHITE);

        if (botSide === SidesExplicit.WHITE)
            requestStockfishMove(startingFEN);
    }

    async function handleResign() {
        const resignBtn = document.getElementById('resign-button') as HTMLButtonElement;
        resignBtn.disabled = true;
        try {
            const resignCall = await fetch(`/api/bot/end/${botGameId}`, { method: 'POST' });
            if (resignCall.ok) {
                sessionStorage.removeItem('botGameId');
                //add resign
            }
        } catch(e) {
            resignBtn.disabled = false;
        }
    }

    document.getElementById('playAsWhite')?.addEventListener('click', () => startBotGame(SidesExplicit.BLACK));
    document.getElementById('playAsBlack')?.addEventListener('click', () => startBotGame(SidesExplicit.WHITE));
    onButtonClick('resign-button', handleResign);
});