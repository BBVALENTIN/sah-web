import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";
import {MoveList} from "./MoveList.js";

const canvas: HTMLCanvasElement = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx = canvas.getContext('2d');
if(!ctx) throw new Error("No canvas context");

const tabla: Tabla = new Tabla(ctx);
export const moveList:MoveList = new MoveList("move-list");
const mouse: Mouse = new Mouse(canvas, tabla);


async function loadBoard()
{
    const response: Response = await fetch('/api/chess/state');
    const piecesData = await response.json();

    tabla.setPiecesFromServer(piecesData);
    tabla.redesenare();

    await mouse.getTurn();
}

loadBoard();