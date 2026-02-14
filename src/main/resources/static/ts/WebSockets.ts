import {Message, userInfo} from "./Types.js";
import {MessageType} from "./Enums.js";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs"

//LOGICA FRONTEND CHAT + BASIC
let loggedUsername: string = "";
let userId: number = 0;
let stompClient: Client;
let messageInput = document.querySelector('#message') as HTMLInputElement;
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

function connect():void{


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
        debug: (str) => console.log("str"),
        reconnectDelay: 5000
    });
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);

    stompClient.publish({
        destination: "/app/chat.addUser",
        body: JSON.stringify({sender: loggedUsername, type: 'JOIN'})
    });
}

function sendMessage() {
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        let chatMessage: Message = {
            sender: loggedUsername,
            content: messageContent,
            type: MessageType.CHAT
        };
    }
}

function onMessageReceived() {

}