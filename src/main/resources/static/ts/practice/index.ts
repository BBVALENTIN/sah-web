import {minimalState} from "../Types.js";
import {Tabla} from "../Tabla.js";
import {MoveList} from "../MoveList.js";
import {MousePractice} from "./MousePractice.js";

let loggedUsername: string;
const canvas = document.getElementById('chessCanvas') as HTMLCanvasElement;
const ctx: CanvasRenderingContext2D = canvas.getContext('2d')!;
export const tabla = new Tabla(ctx);
await tabla.loadImages();
export const moveList:MoveList = new MoveList("move-list");
const mousePractice = new MousePractice(canvas, tabla);

const size: number = Tabla.squareSize * 8;
canvas.width = size;
canvas.height = size;

async function getUserInfo() {
    try {
        const response = await fetch('/info/user');
        if (response.ok) {
            const data = await response.json();
            loggedUsername = data.username;
        }
    } catch (e) {
        console.log(e);
    }
}

async function loadBoard() {
    try {
        const response = await fetch('/practiceBoard');
        if(response.ok) {
            const minimalState: minimalState = await response.json();
            const piecesData = minimalState.Piese;
            console.log(piecesData);
            if(minimalState.currentPGN != null )
                moveList.addWholePGN(minimalState.currentPGN);
            tabla.setPiecesFromServer(piecesData);
            tabla.redesenare();
        }
    }
    catch(e) {
        console.error('Eroare: ', e);
    }
}

loadBoard();