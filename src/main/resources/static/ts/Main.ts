import {Tabla} from './Tabla.js';
import {Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
import {connect, culoareCurenta, sendMessage, setCuloareCurenta, state} from "./WebSockets.js"
import {getInfoLobby} from "./APIs.js";
import {minimalState} from "./Types";
import {culoriPiesa} from "./Enums.js";

const currentPlayerSide = document.getElementById('currentPlayer') as HTMLElement;
const otherPlayerSide = document.getElementById('otherPlayer') as HTMLElement;
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

            const username = loggedUsername;
            if(lobbyInfo &&lobbyInfo.playerWhite === username)
            {
                currentPlayerSide.innerText = username;
                otherPlayerSide.innerText = lobbyInfo.playerBlack ? lobbyInfo.playerBlack : 'Waiting . . .';
            }
            else if(lobbyInfo && lobbyInfo.playerBlack === username) {
                currentPlayerSide.innerText = username;
                otherPlayerSide.innerText = lobbyInfo.playerWhite ? lobbyInfo.playerWhite : 'Waiting . . .';
            }
            const lobbyId: string = lobbyInfo.lobbyId;
            const isBlack = (lobbyInfo.playerBlack === loggedUsername);
            console.log("Player is black? ", isBlack);
            await tabla.loadImages();
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
const resignBtn = document.getElementById("resign-button") as HTMLButtonElement;

async function loadBoard(lobbyId: string):Promise<void>
{
    try {
        const response: Response = await fetch(`/api/chess/onlineState/${lobbyId}`);
        const minimalState: minimalState = await response.json();
        console.log(minimalState);
        const piecesData = minimalState.Piese;
        setCuloareCurenta(minimalState.culoareCurenta);
        if(minimalState.currentPGN != null )
            moveList.addWholePGN(minimalState.currentPGN);
        tabla.setPiecesFromServer(piecesData);
        tabla.redesenare();
    }
    catch (e) {
        console.error("Eroare la incarcare de date - loadboard");
    }
}
initializeApp();


resignBtn.addEventListener("click", async () => {
    resignBtn.disabled = true;
    const action = resignBtn.innerText === 'Resign' ? 'resign' : 'abort'
    try {
        console.log(lobbyInfo?.lobbyId);
        let resignColor:culoriPiesa;
        if(lobbyInfo?.playerBlack == loggedUsername)
            resignColor = culoriPiesa.NEGRU;
        else
            resignColor = culoriPiesa.ALB;

        const responseResign: Response = await fetch(`api/chess/${action}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ lobbyId: lobbyInfo?.lobbyId, username: loggedUsername, color: resignColor })
        });

        if(responseResign.ok) {
            moveList.resign(culoareCurenta!);
            if(state.stompClient && state.connected) {
                state.stompClient.disconnect(() => {
                    console.log("Disconnected from server due to resignation/abort");
                    state.connected = false
                });
            }
            if(action === 'abort')
                window.location.href = '/lobbies';
        }
        else {
            resignBtn.disabled = false;
        }
    }catch (e) {
        resignBtn.disabled = false;
        console.error("Eroare - contacteaza un dev!")
    }
});