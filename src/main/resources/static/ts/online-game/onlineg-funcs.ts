import {Board} from '../tools/Board.js';
import {Mouse} from "./Mouse.js";
import {MoveList} from "../tools/MoveList.js";
import {connect, currentColor, sendMessage, setCurrentColor, state} from "./WebSockets.js"
import {lobbyInfo, minimalState} from "../tools/Types.js";
import {SidesExplicit} from "../tools/Enums";

const currentPlayerSide = document.getElementById('currentPlayer') as HTMLElement;
const otherPlayerSide = document.getElementById('otherPlayer') as HTMLElement;
const lobbyInfo = await getInfoLobby();
const lobbyId = lobbyInfo?.lobbyId;
const loggedUsername = document.body.dataset.username;
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

            const messageInput = document.getElementById('message') as HTMLInputElement;

            messageInput.addEventListener('keydown', (e: KeyboardEvent) => {
                if(e.code === 'Enter') {
                    sendMessage();
                    e.preventDefault();
                }
            });

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
            connect(lobbyId);
            await loadBoard(lobbyId);
        }
    } catch (e) {
        console.error("Failed to fetch user info", e);
    }


    resignBtn.addEventListener("click", async () => {
        resignBtn.disabled = true;
        try {

            const responseResign: Response = await fetch(`/api/chess/EndGameEarly/${lobbyInfo?.lobbyId}`, {
                method: 'POST',
            });

            if(responseResign.ok) {
                if(state.stompClient && state.connected) {
                    await state.stompClient.deactivate();

                    console.log("Disconnected from server due to resignation - test log");
                    state.connected = false;
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

export const board: Board = new Board('chessCanvas');
export const moveList:MoveList = new MoveList("move-list");

export const mouse: Mouse = new Mouse(board.getCanvas(), board);
const resignBtn = document.getElementById("resign-button") as HTMLButtonElement;
async function loadBoard(lobbyId: string):Promise<void>
{
    try {
        const response: Response = await fetch(`/api/chess/onlineState/${lobbyId}`);
        const minimalState: minimalState = await response.json();
        const currentFEN = minimalState.currentFEN;
        if(minimalState.currentPGN != null )
            moveList.addWholePGN(minimalState.currentPGN);
        board.setPiecesFromFEN(currentFEN);
        setCurrentColor(parseColorFromFEN(currentFEN));
        board.redraw();
    }
    catch (e) {
        console.error("There was an error loading the data");
    }
}

function parseColorFromFEN(FEN: string): SidesExplicit {
    const split:string[] = FEN.split(' ');
    if(split[1] == 'w')
        return SidesExplicit.WHITE;
    else
        return SidesExplicit.BLACK;

    throw new Error("FEN error, can't read character: " + split[1]);
}

export async function getInfoLobby(): Promise<lobbyInfo | undefined> {
    const response = await fetch('/info/lobby');
    if(response.ok) {
        return await response.json();
    }
    return undefined;
}