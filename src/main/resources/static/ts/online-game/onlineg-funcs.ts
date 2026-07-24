import {Board} from '../tools/Board.js';
import {Mouse} from "./Mouse.js";
import {MoveList} from "../tools/MoveList.js";
import {connect, sendMessage, state} from "./WebSockets.js"
import {getInfoLobby} from "../misc/APIs.js";
import {minimalState} from "../tools/Types.js";

const currentPlayerSide = document.getElementById('currentPlayer') as HTMLElement;
const otherPlayerSide = document.getElementById('otherPlayer') as HTMLElement;
const lobbyInfo = await getInfoLobby();
const lobbyId = lobbyInfo?.lobbyId;
let loggedUsername = document.body.dataset.username;
if(!lobbyId) {
    console.log("Lobby ID not found");
}

export async function initializeApp() {
    try {
        const lobbyInfo = await getInfoLobby();
        if (lobbyInfo) {

            const username = loggedUsername;
            if(!username)
            {
                console.error("Username not found");
                return;
            }
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
            await board.loadImages();
            board.setOrientation(isBlack);
            board.redraw();
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

    resignBtn.addEventListener("click", async () => {
        resignBtn.disabled = true;
        try {

            const responseResign: Response = await fetch(`/api/chess/EndGameEarly/${lobbyInfo?.lobbyId}`, {
                method: 'POST',
            });

            if(responseResign.ok) {
                if(state.stompClient && state.connected) {
                    state.stompClient.disconnect(() => {
                        console.log("Disconnected from server due to resignation/abort");
                        state.connected = false
                    });
                }
            }
            else {
                resignBtn.disabled = false;
            }
        }catch (e) {
            resignBtn.disabled = false;
            console.error("Error regarding the resign button!")
        }
    });
}

const canvas: HTMLCanvasElement = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx: CanvasRenderingContext2D = canvas.getContext('2d')!;

const size: number = Board.squareSize * 8;
canvas.width = size;
canvas.height = size;

export const board: Board = new Board(ctx);
export const moveList:MoveList = new MoveList("move-list");

export const mouse: Mouse = new Mouse(canvas, board);
const resignBtn = document.getElementById("resign-button") as HTMLButtonElement;
async function loadBoard(lobbyId: string):Promise<void>
{
    try {
        const response: Response = await fetch(`/api/chess/onlineState/${lobbyId}`);
        const minimalState: minimalState = await response.json();
        const piecesData = minimalState.Pieces;
        if(minimalState.currentPGN != null )
            moveList.addWholePGN(minimalState.currentPGN);
        board.setPiecesFromServer(piecesData);
        board.redraw();
    }
    catch (e) {
        console.error("There was an error loading the data");
    }
}