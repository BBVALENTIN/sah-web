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

    await mutareMouse.getTurn();

    const statusDiv = document.getElementById("status");
    statusDiv.innerText = "Culoarea curenta muta: " +
        (mutareMouse.culoareCurenta === 1 ? "Alb" : "Negru");

    const destination = document.getElementById("movesPGN");
    if(destination) {
        destination.innerText =  await mutareMouse.getPGN();
        ;
    }
}

const resetBtn = document.getElementById("reset-btn")
resetBtn.addEventListener("click", async () => {
    try{
        const resp = await fetch(`/api/chess/reset`)
        if(!resp.ok) {
            console.log("eroare la reset");
            return;
        }

        const piecesData = await resp.json();

        loadBoard();

        const statusDiv = document.getElementById("status");
        statusDiv.innerText = "Tabla a fost resetata";

        console.log("RESET BOARD OK");
    } catch (err) {
        console.log("eroare eroare eroare");
    }

})
loadBoard();