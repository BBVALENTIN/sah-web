import {lobbyInfo, Message, Mutare_Reusita} from "../tools/Types.js";
import {MessageType, SidesExplicit} from "../tools/Enums.js";
import {mouse, moveList, board} from "./Main.js";

declare var SockJS: any;
declare var Stomp: any;

//LOGICA FRONTEND CHAT + BASIC
let loggedUsername: string = "";
let userId: number = 0;
let currentLobbyId: string = "";
export let culoareCurenta: SidesExplicit = SidesExplicit.WHITE;
let resignBtn = document.getElementById('resign-button') as HTMLButtonElement;

export const state = {
    stompClient: null as any,
    connected: false
};
let messageInput = document.querySelector('#message') as HTMLInputElement;
let chatMessage: Message;
let messageArea = document.querySelector("#messageArea") as HTMLElement;
if(!messageInput) {
    alert("Nu poti trimite mesaje goale");
}

export function connect(username: string, lobbyId: string):void{
    if(!username) {
        alert("Error - not seeing the user");
        return;
    }

    loggedUsername = username;
    currentLobbyId = lobbyId;
    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);

    state.stompClient.connect(
        { username: loggedUsername},
        function () {
            state.connected = true;

            console.log("Connected");

            state.stompClient.subscribe(`/topic/chat/${lobbyId}`, onMessageReceived);
            state.stompClient.subscribe(`/topic/game/${lobbyId}`, onMoveReceived);
            state.stompClient.subscribe(`/topic/resign-lobby/${lobbyId}`, function (payload: any) {
                const winnerSide = JSON.parse(payload.body)
                const resignButton = document.getElementById('resign-button') as HTMLButtonElement;
                resignButton.disabled = true;
                console.log(winnerSide)
                if(winnerSide === "BLACK") {
                    moveList.addMove("0-1");
                } else if(winnerSide === "WHITE") {
                   moveList.addMove("1-0");
                }
                mouse.soundManager.play("end");
            });
            state.stompClient.subscribe(`/topic/lobby/${lobbyId}`, function (payload: any) {
                const updatedLobby: lobbyInfo = JSON.parse(payload.body);
                updatePlayerNamesUI(updatedLobby);
            });
            state.stompClient.subscribe(`/user/queue/errors`, onErrorsReceived);

            state.stompClient.send(`/app/chat.addUser/${lobbyId}`, {}, JSON.stringify({sender:loggedUsername, type: MessageType.JOIN })
            );

            if(moveList.getMoveCount() < 2)
                resignBtn.innerText = 'Abort';
            else
                resignBtn.innerText = 'Resign';
        },
        function(error: any) {
            state.connected = false;
            console.error("STOMP ERROR: ", error);
        }
    )
}

function updatePlayerNamesUI(lobbyInfo: lobbyInfo) {
    const currentPlayerSide = document.getElementById('currentPlayer') as HTMLElement;
    const otherPlayerSide = document.getElementById('otherPlayer') as HTMLElement;
    if(lobbyInfo &&lobbyInfo.playerWhite === loggedUsername)
    {
        currentPlayerSide.innerText = loggedUsername;
        otherPlayerSide.innerText = lobbyInfo.playerBlack ? lobbyInfo.playerBlack : 'Waiting . . .';
    }
    else if(lobbyInfo && lobbyInfo.playerBlack === loggedUsername) {
        currentPlayerSide.innerText = loggedUsername;
        otherPlayerSide.innerText = lobbyInfo.playerWhite ? lobbyInfo.playerWhite : 'Waiting . . .';
    }
}

export function sendMessage() {
    const messageContent = messageInput.value.trim();
    if(messageContent && state.stompClient && state.connected) {
        chatMessage = {
            sender: loggedUsername,
            content: messageContent,
            type: MessageType.CHAT,
        };

        state.stompClient.send(`/app/chat.sendMessage/${currentLobbyId}`, {}, JSON.stringify(chatMessage));

        messageInput.value = "";
    }
}

function onMessageReceived(payload: any) {
    let message = JSON.parse(payload.body);
    let messageElement = document.createElement("div");
    if(message.type === MessageType.JOIN) {
        messageElement.classList.add("event-message");
        messageElement.innerHTML = `<div>${message.sender} joined!</div>`;
    }
    else if(message.type === MessageType.LEAVE) {
        messageElement.classList.add("event-message");
        messageElement.innerHTML = `<div>${message.sender} left the lobby!</div>`;
    }
    else { // MessageType.CHAT
        messageElement.classList.add("chat-message");
        messageElement.innerHTML = `<div class="message-content">${message.sender}: ${message.content}</div>`;
    }
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onMoveReceived(payload: any) {
    const result: Mutare_Reusita = JSON.parse(payload.body);
    if(result.lastMove.fromRow !== undefined && result.lastMove.toRow !== undefined) {
        board.setLastMove(result.lastMove.fromRow, result.lastMove.fromCol, result.lastMove.toRow, result.lastMove.toCol);
    }
    board.setPiecesFromServer(result.updatedPieces);
    culoareCurenta = result.currentColor;
    moveList.addMove(result.pgn);
    if(moveList.getMoveCount() < 2)
        resignBtn.innerText = 'Abort';
    else
        resignBtn.innerText = 'Resign';
    board.redraw();

    if (result.checkmate) {
        mouse.soundManager.play("checkmate");
        mouse.soundManager.play("end");
        resignBtn.disabled = true;
    } else if (result.check) {
        mouse.soundManager.play("check");
    } else if (result.captures) {
        mouse.soundManager.play("capture");
    } else {
        mouse.soundManager.play("move");
    }
}

function onErrorsReceived(payload: any) {
    const errorCodes = JSON.parse(payload.body);
    board.redraw();
}

export function setCuloareCurenta(culoare: SidesExplicit) {
    culoareCurenta = culoare;
}