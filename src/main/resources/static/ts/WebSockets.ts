import {Message, userInfo} from "./Types.js";
import {MessageType} from "./Enums.js";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs"

//LOGICA FRONTEND CHAT + BASIC
let loggedUsername: string = "";
let userId: number = 0;
export let stompClient: Client;
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

export function connect(event: Event):void{
    if(!loggedUsername) {
        alert("Error - not seeing the user");
        return;
    }

    stompClient = new Client({
        brokerURL: undefined,
        connectHeaders: {
            username: loggedUsername
        },
        webSocketFactory: () => new SockJS('/ws'),
        debug: (str) => console.log(str),
        reconnectDelay: 5000
    });

    stompClient.onConnect = onConnected;

    stompClient.onWebSocketError = (error) => {
        console.error("Websocket error: ", error);
    }

    stompClient.onStompError = (frame) => {
        console.error("Broker error", frame.headers['message']);
    }

    stompClient.activate();

    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    stompClient.publish({
        destination: "/app/chat.addUser",
        body: JSON.stringify({type: MessageType.JOIN})
    });
}

export function sendMessage() {
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        chatMessage = {
            sender: loggedUsername,
            content: messageContent,
            type: MessageType.CHAT
        };
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
        messageElement.innerHTML = `<div class="message-content">${message.content}</div>`;
    }
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}