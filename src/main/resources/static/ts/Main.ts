import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";
import {userInfo} from "./Types";

//LOGICA FRONTEND CHAT + BASIC
let username: string = "";
let userId: number = 0;

let respInfo = await fetch("/info/user");
if(respInfo.ok) {
    const userInfoJSON:userInfo = await respInfo.json();
    username = userInfoJSON.username;
    userId = userInfoJSON.userId;
    console.log("username: ", username, "userId: ", userId);
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