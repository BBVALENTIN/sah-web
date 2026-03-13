import {lobbyInfo, Message, Mutare_Reusita, userInfo} from "./Types.js";
import {LobbyType, MessageType} from "./Enums.js";
import {mouse, moveList, tabla} from "./Main.js";
import {getAllPGN, getInfoLobby, globalLobbyInfo} from "./APIs.js";
import {Tabla} from "./Tabla";

declare var SockJS: any;
declare var Stomp: any;

//LOGICA FRONTEND CHAT + BASIC
let loggedUsername: string = "";
let userId: number = 0;

export const state = {
    stompClient: null as any,
    connected: false
};
let messageInput = document.querySelector('#message') as HTMLInputElement;
let chatMessage: Message;
let messageArea = document.querySelector("#messageArea") as HTMLElement;
let PGN = getAllPGN(); // will be used in case of disconnection
if(!messageInput) {
    alert("Nu poti trimite mesaje goale");
}

export function connect(username: string, lobbyId: string):void{
    if(!username) {
        alert("Error - not seeing the user");
        return;
    }

    loggedUsername = username;

    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);

    state.stompClient.connect(
        { username: loggedUsername},
        function () {
            state.connected = true;

            console.log("Connected");

            state.stompClient.subscribe(`/topic/chat/${lobbyId}`, onMessageReceived);
            state.stompClient.subscribe(`/topic/game/${lobbyId}`, onMoveReceived);
            state.stompClient.subscribe(`/topic/lobby/${lobbyId}`, function (payload: any) {
                const updatedLobby: lobbyInfo = JSON.parse(payload.body);
                updatePlayerNamesUI(updatedLobby);
            });
            state.stompClient.subscribe(`/user/queue/errors`, onErrorsReceived);

            state.stompClient.send('/app/chat.addUser', {}, JSON.stringify({sender:loggedUsername, type: MessageType.JOIN })
            );
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
            type: MessageType.CHAT
        };

        state.stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));

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
        tabla.setLastMove(result.lastMove.fromRow, result.lastMove.fromCol, result.lastMove.toRow, result.lastMove.toCol);
    }
    console.log("MUTARE REUSITA? ", result);
    tabla.setPiecesFromServer(result.updatedPieces);
    mouse.culoareCurenta = result.culoareCurenta;
    moveList.addMove(result.pgn);
    tabla.redesenare();

    if (result.checkmate) {
        mouse.soundManager.play("checkmate");
        mouse.soundManager.play("end");
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
    tabla.redesenare();
}