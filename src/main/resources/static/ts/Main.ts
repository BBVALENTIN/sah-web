import { Tabla } from './Tabla.js';
import { Mouse} from "./Mouse.js";

const canvas: HTMLCanvasElement = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx = canvas.getContext('2d');
if(!ctx) throw new Error("No canvas context");

const tabla: Tabla = new Tabla(ctx);

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