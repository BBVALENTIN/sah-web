import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
import { connect, sendMessage } from "./WebSockets.js"
import {lobbyInfo} from "./Types.js";
import {getInfoLobby} from "./APIs.js";

const lobbyInfo = await getInfoLobby();
const lobbyId = lobbyInfo?.lobbyId;
console.log(lobbyId);
if(!lobbyId) {
    console.log("nu avem lobbyId");
}
// LOGICA FRONTEND MESAJE
async function initializeApp() {
    let loggedUsername = "";
    try {
        const lobbyInfo = await getInfoLobby();
        if (lobbyInfo != undefined) {
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
    try {
        const response: Response = await fetch(`/api/chess/onlineState/${lobbyId}`);
        const piecesData = await response.json();

        tabla.setPiecesFromServer(piecesData);
        tabla.redesenare();

        await mouse.getTurn();
    }
    catch (e) {
        console.error("Eroare la incarcare de date - loadboard");
    }
}
initializeApp();
loadBoard();

document.addEventListener("DOMContentLoaded", async () => {
    let playerWhite = document.getElementById('chessPlayerWhite') as HTMLDivElement;
    let playerBlack = document.getElementById('chessPlayerBlack') as HTMLDivElement;
    resignBtn.addEventListener("click", () => {
        moveList.resign(mouse.culoareCurenta!);
    });
    try {
        if(lobbyInfo != undefined) {
            playerWhite.innerText = lobbyInfo.playerWhite;
            playerBlack.innerText = lobbyInfo.playerBlack;
        }
    }
    catch(e) {
        console.log(e);
    }
});