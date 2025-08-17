import { Tabla } from './tabla.js';
import {MutareMouse} from "./MutareMouse.js";


const canvas = document.getElementById('chessCanvas');
const ctx = canvas.getContext('2d');


const tabla = new Tabla(ctx);

const mutareMouse = new MutareMouse(canvas, tabla);

async function loadBoard() {
    const response = await fetch('/api/chess/state');
    const piecesData = await response.json();

    tabla.setPiecesFromServer(piecesData);

    tabla.redesenare();
}

loadBoard();