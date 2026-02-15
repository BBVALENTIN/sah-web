import {Message, userInfo} from "./Types.js";
import {MessageType} from "./Enums.js";

//LOGICA FRONTEND CHAT + BASIC
let loggedUsername: string = "";
let userId: number = 0;
declare var SockJS: any;
declare var Stomp: any;
export let stompClient: any = null;
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

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect(
        { username: loggedUsername},
        function () {
            console.log("Connected");
            stompClient.subscribe('/topic/public', onMessageReceived);

            stompClient.send('/app/chat.addUser', {}, JSON.stringify({ type: MessageType.JOIN })
            );
        },
        function(error: any) {
            console.error("STOMP ERROR: ", error);
        }
    )

    event.preventDefault();
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
    const messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        chatMessage = {
            sender: loggedUsername,
            content: messageContent,
            type: MessageType.CHAT
        };

        stompClient.send("app/chat.sendMessage", {}, JSON.stringify(chatMessage));

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
        messageElement.innerHTML = `<div class="message-content">${message.content}</div>`;
    }
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}