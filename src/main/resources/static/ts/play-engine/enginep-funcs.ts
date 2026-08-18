import {AppCore, getLoggedUsername, initBoard, initCanvas, onButtonClick} from "../tools/chess-utils.js";
import {SidesExplicit} from "../tools/Enums.js";
import {MouseBot} from "./mouse-bot.js";

export let engineCore: AppCore;
interface botResponse {
    gameId: string;
    currentFEN: string;
}

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
            await mouseBot.applyBotMove(bestMove);
    }
}

export async function initEngineApp() {

    const savedGameId = sessionStorage.getItem('botGameId');
    engineCore = initCanvas('chessCanvas', 'move-list');
    await initBoard(engineCore.board);

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
                engineCore.moveList.addWholePGN(state.currentPGN);
            engineCore.board.setPiecesFromFEN(state.currentFEN)
            engineCore.board.redraw();

            const botSide = state.botSide as SidesExplicit;
            mouseBot = new MouseBot(
                engineCore.board.getCanvas(), engineCore.board, gameId, engineCore.moveList,
                botSide === SidesExplicit.BLACK ? SidesExplicit.WHITE : SidesExplicit.BLACK
            );
            mouseBot.onEngineRequest = (fen: string) => requestStockfishMove(fen);
            engineCore.board.setOrientation(botSide === SidesExplicit.WHITE);
        } catch(e) {
            console.error('Resuming error:', e);
        }
    }

    function setText(id: string, text: string) {
        const el = document.getElementById(id);
        if(el) el.innerText = text;
    }

    async function startBotGame(botSide: SidesExplicit) {
        document.getElementById('side-select-overlay')?.remove();
        const isPlayerPlayingBlack: boolean = botSide === SidesExplicit.WHITE;
        engineCore.board.setOrientation(isPlayerPlayingBlack);
        const username = await getLoggedUsername();

        const playerBlack = isPlayerPlayingBlack ? username : 'Stockfish';
        const playerWhite = isPlayerPlayingBlack ? 'Stockfish' : username;

        setText('player-black', playerBlack);
        setText('player-white', playerWhite);
        const response = await fetch(`/api/bot/start?botSide=${botSide}`, { method: 'POST' });

        const data: botResponse = await response.json();

        botGameId = data.gameId;
        engineCore.board.setPiecesFromFEN(data.currentFEN);
        engineCore.board.redraw(); // will make a function for this
        sessionStorage.setItem('botGameId', botGameId);

        mouseBot = new MouseBot(
            engineCore.board.getCanvas(), engineCore.board, botGameId, engineCore.moveList,
            botSide === SidesExplicit.BLACK ? SidesExplicit.WHITE : SidesExplicit.BLACK
        );

        mouseBot.onEngineRequest = (fen: string) => requestStockfishMove(fen);

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
                mouseBot.moveList.addMove(await resignCall.text());
            }
        } catch(e) {
            resignBtn.disabled = false;
        }
    }

    document.getElementById('playAsWhite')?.addEventListener('click', () => startBotGame(SidesExplicit.BLACK));
    document.getElementById('playAsBlack')?.addEventListener('click', () => startBotGame(SidesExplicit.WHITE));
    onButtonClick('resign-button', handleResign);
}