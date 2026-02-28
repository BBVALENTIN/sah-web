import {lobbyInfo, Message, Mutare_Reusita, userInfo} from "./Types.js";
import {MessageType} from "./Enums.js";
import {mouse, moveList, tabla} from "./Main.js";
import {getAllPGN} from "./APIs.js";

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

export function connect(loggedUsername: string, lobbyId: string):void{
    if(!loggedUsername) {
        alert("Error - not seeing the user");
        return;
    }

    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);

    state.stompClient.connect(
        { username: loggedUsername},
        function () {
            state.connected = true;

            console.log("Connected");
            state.stompClient.subscribe('/topic/public', onMessageReceived);
            state.stompClient.subscribe(`/topic/game/${lobbyId}`, onMoveReceived);
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

// function onConnected() {
//     stompClient.subscribe('/topic/public', onMessageReceived);
//
//     stompClient.publish({
//         destination: "/app/chat.addUser",
//         body: JSON.stringify({type: MessageType.JOIN})
//     });
// }

export function sendMessage() {
    console.log("stompClient214:", state.stompClient);
    console.log("connected:", state.stompClient?.connected);
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