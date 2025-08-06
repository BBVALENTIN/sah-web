import { Tabla } from './tabla.js';
import {MutareMouse} from "./MutareMouse.js";


const canvas = document.getElementById('chessCanvas');
const ctx = canvas.getContext('2d');


const tabla = new Tabla(ctx);
tabla.setPiese();
tabla.drawTabla();

const mutareMouse = new MutareMouse(canvas, tabla);
let piesaSelectata = mutareMouse.getPiesaSelectata();
