import {lobbyInfo, Message, OptimisedMove} from "../tools/Types.js";
import {MessageType, SidesExplicit} from "../tools/Enums.js";
import {mouse, moveList, board} from "./onlineg-funcs.js";
import {Client} from "@stomp/stompjs";
import SockJS from "sockjs-client";

// should change this
let loggedUsername = document.body.dataset.username;
let currentLobbyId: string = "";
export let currentColor: SidesExplicit = SidesExplicit.WHITE;
let resignBtn = document.getElementById('resign-button') as HTMLButtonElement;
const pgnOutput = document.getElementById('PGN') as HTMLInputElement;
const fenOutput = document.getElementById('FEN') as HTMLTextAreaElement;
export const state = {
    stompClient: null as Client | null,
    connected: false
};
let messageInput = document.querySelector('#message') as HTMLInputElement;
let chatMessage: Message;
let messageArea = document.querySelector("#messageArea") as HTMLElement;
if(!messageInput) {
    alert("Can't send empty messages");
}

export function connect(lobbyId: string):void{

    currentLobbyId = lobbyId;

    const client = new Client({
        webSocketFactory: () => new SockJS('/ws'),

        onConnect: (frame) => {
            state.connected = true;
            console.log("Connected: ", frame);

            client.subscribe(`/topic/chat/${lobbyId}`, onMessageReceived);

            client.subscribe(`/topic/game/${lobbyId}`, onMoveReceived);

            client.subscribe(`/topic/resign-lobby/${lobbyId}`, (payload:any) => {
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

            client.subscribe(`/topic/lobby/${lobbyId}`, (payload: any) => {
               const updatedLobby: lobbyInfo = JSON.parse(payload.body);
               updatePlayerNamesUI(updatedLobby);
            });

            client.subscribe(`/user/queue/errors`, onErrorsReceived);

            client.publish({
                destination: `/app/chat.addUser/${lobbyId}`,
                body: JSON.stringify({
                    type: MessageType.JOIN
                })
            });
        },

        onStompError: (frame) => {
            state.connected = false;

            console.error("STOMP ERROR - this shouldn't be happening");
            console.error("Message: ", frame.headers['message']);
            console.error("Body", frame.body);
        },

        onWebSocketError: (error) => {
            state.connected = false;

            console.error("WebSocket error - this shouldn't be happening, ", error);
        },

        onWebSocketClose: () => {
            state.connected = false;

            console.log("Websocket close");
        }
    });

    state.stompClient = client;
    client.activate();
}

function updatePlayerNamesUI(lobbyInfo: lobbyInfo) {
    const currentPlayerSide = document.getElementById('currentPlayer') as HTMLElement;
    const otherPlayerSide = document.getElementById('otherPlayer') as HTMLElement;
    if(lobbyInfo && lobbyInfo.playerWhite === loggedUsername)
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
            content: messageContent,
            type: MessageType.CHAT,
        };

        state.stompClient.publish({
            destination:  `/app/chat.sendMessage/${currentLobbyId}`,
            body: JSON.stringify(chatMessage)
        });

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
    else {
        messageElement.classList.add("chat-message");
        messageElement.innerHTML = `<div class="message-content">${message.sender}: ${message.content}</div>`;
    }
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onMoveReceived(payload: any) {
    const result: OptimisedMove = JSON.parse(payload.body);
    if(result.lastMoveCoords.fromRow !== undefined && result.lastMoveCoords.targetRow !== undefined) {
        board.setLastMove(result.lastMoveCoords.fromRow, result.lastMoveCoords.fromCol, result.lastMoveCoords.targetRow, result.lastMoveCoords.targetCol);
    }
    board.setPiecesFromFEN(result.fen);
    currentColor = result.currentColor;
    moveList.addMove(result.pgn);
    if(moveList.getMoveCount() < 2)
        resignBtn.innerText = 'Abort';
    else
        resignBtn.innerText = 'Resign';
    board.redraw();

    if (result.checkMate) {
        mouse.soundManager.play("checkmate");
        mouse.soundManager.play("end");
        resignBtn.disabled = true;
    } else if (result.check) {
        mouse.soundManager.play("check");
    } else if (result.pgn.includes('x')) {
        mouse.soundManager.play("capture");
    } else {
        mouse.soundManager.play("move");
    }
    addMoveToCopyablePGN(result.pgn);
    fenOutput.innerText = result.fen;
}

function onErrorsReceived(payload: any) {
    const errorCodes = JSON.parse(payload.body);
    board.redraw();
}

export function setCurrentColor(color: SidesExplicit) {
    currentColor = color;
}

function addMoveToCopyablePGN(currentPGN: string): void {
        const moveNumber = moveList.getMoveCount();
        let currentMove: string;
        if(moveNumber % 2 == 0)
            currentMove = (moveNumber / 2).toString() + ". " + currentPGN;
        else
            currentMove = " " + currentPGN + " ";

        pgnOutput.value += currentMove;
    }