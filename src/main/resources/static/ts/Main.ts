import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
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

// LOGICA FRONTEND JOC
const canvas: HTMLCanvasElement = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx: CanvasRenderingContext2D = canvas.getContext('2d')!;

const size: number = Tabla.squareSize * 8;
canvas.width = size;
canvas.height = size;

const tabla: Tabla = new Tabla(ctx);
export const moveList:MoveList = new MoveList("move-list");

export const mouse: Mouse = new Mouse(canvas, tabla);
const resignBtn = document.getElementById("resign-button")!;

async function loadBoard():Promise<void>
{
    const response: Response = await fetch('/api/chess/state');
    const piecesData = await response.json();

    tabla.setPiecesFromServer(piecesData);
    tabla.redesenare();

    await mouse.getTurn();
}

loadBoard();

document.addEventListener("DOMContentLoaded", () => {
    resignBtn.addEventListener("click", () => {
        moveList.resign(mouse.culoareCurenta!);
    });
});