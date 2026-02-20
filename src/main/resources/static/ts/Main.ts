import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
import { connect, sendMessage } from "./WebSockets.js"
import {lobbyInfo} from "./Types";


// LOGICA FRONTEND MESAJE
async function initializeApp() {
    let loggedUsername = "";
    try {
        const respInfo = await fetch("/info/user");
        if (respInfo.ok) {
            const lobbyInfo: lobbyInfo = await respInfo.json();
            loggedUsername = lobbyInfo.loggedUsername.username;
            const lobbyId: string = lobbyInfo.lobbyId;
            connect(loggedUsername, lobbyId);
        }
    } catch (e) {
        console.error("Failed to fetch user info", e);
    }

    let sendButton = document.querySelector('#sendMessageButton') as HTMLButtonElement;

    sendButton.addEventListener('click', (event) => {
        event.preventDefault();
        sendMessage();
    });
}

// LOGICA FRONTEND JOC
const canvas: HTMLCanvasElement = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx: CanvasRenderingContext2D = canvas.getContext('2d')!;

const size: number = Tabla.squareSize * 8;
canvas.width = size;
canvas.height = size;

export const tabla: Tabla = new Tabla(ctx);
export const moveList:MoveList = new MoveList("move-list");

export const mouse: Mouse = new Mouse(canvas, tabla);
const resignBtn = document.getElementById("resign-button")!;

async function loadBoard():Promise<void>
{
    const response: Response = await fetch('/api/chess/state');
    const piecesData = await response.json();

    tabla.setPiecesFromServer(piecesData);
    tabla.redesenare();

    await mouse.getTurn();
}
initializeApp();
loadBoard();

document.addEventListener("DOMContentLoaded", () => {
    resignBtn.addEventListener("click", () => {
        moveList.resign(mouse.culoareCurenta!);
    });
});