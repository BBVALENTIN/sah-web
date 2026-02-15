import {Message, userInfo} from "./Types.js";
import {MessageType} from "./Enums.js";

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
if(!messageInput) {
    alert("Nu poti trimite mesaje goale");
}


let respInfo = await fetch("/info/user");
if(respInfo.ok) {
    const userInfoJSON:userInfo = await respInfo.json();
    loggedUsername = userInfoJSON.username;
    userId = userInfoJSON.userId;
    console.log("username: ", loggedUsername, "userId: ", userId);
}

export function connect(loggedUsername: string):void{
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