import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
import { connect, sendMessage } from "./WebSockets.js"
import {getInfoLobby} from "./APIs.js";

const pWhite = document.getElementById('chessPlayerWhite');
const pBlack = document.getElementById('chessPlayerBlack');
const lobbyInfo = await getInfoLobby();
const lobbyId = lobbyInfo?.lobbyId;
let loggedUsername = "";
console.log(lobbyId);
if(!lobbyId) {
    console.log("nu avem lobbyId");
}
try{
    const response = await fetch('/info/user');
    if(response.ok) {
        const data = await response.json();
        loggedUsername = data.username;
    }
}
catch (e) {
    console.log(e);
}
// LOGICA FRONTEND MESAJE
async function initializeApp() {
    try {
        const lobbyInfo = await getInfoLobby();
        if (lobbyInfo) {

            if(pWhite) pWhite.innerText = lobbyInfo.playerWhite || "Waiting...";
            if(pBlack) pBlack.innerText = lobbyInfo.playerBlack || "Waiting...";

            const username = loggedUsername;
            const lobbyId: string = lobbyInfo.lobbyId;
            const isBlack = (lobbyInfo.playerBlack === loggedUsername);
            console.log("Player is black? ", isBlack);
            tabla.setOrientare(isBlack);
            tabla.redesenare();
            connect(username, lobbyId);
            await loadBoard(lobbyId);
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

async function loadBoard(lobbyId: string):Promise<void>
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


resignBtn.addEventListener("click", () => {
    moveList.resign(mouse.culoareCurenta!);
});