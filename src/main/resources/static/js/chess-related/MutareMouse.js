import { Tabla } from "./tabla.js";
import {SoundManager} from "./audio/soundManager.js";

export class MutareMouse {
    constructor(canvas, tabla) {
        this.canvas = canvas;
        this.tabla = tabla;
        this.piesaSelectata = null;
        this.winner = null;
        this.offsetX = 0;
        this.offsetY = 0;

        this.soundManager = new SoundManager();
        canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        canvas.addEventListener("mousemove", this.onMouseMove.bind(this));
        canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    async getTurn()
    {
        const resp = await fetch(`api/chess/turn`)
        if(resp.ok)
            this.culoareCurenta = await resp.json();
    }

    convertNumberToSide(number)
    {
        return number === 1 ? "alb" : "negru";
    }

    async getPGN()
    {
        const resp = await fetch(`api/chess/PGN`)
        let PGN = null
        if(resp.ok)
            PGN = await resp.text();

        return PGN;
    }


    getSquareFromMouse(e) {
        const rect = this.canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const col = Math.floor(x / Tabla.squareSize);
        const row = Math.floor(y / Tabla.squareSize);
        return { col, row, x, y };
    }

    async onMouseDown(e) {
        const { col, row, x, y } = this.getSquareFromMouse(e);
        console.log("Culoarea curenta: ", this.culoareCurenta);
        const piesa = this.tabla.getPiesa(row, col);
        if (piesa && piesa.color === this.culoareCurenta) {
            this.piesaSelectata = piesa;
            piesa.isDragging = true;
            piesa.dragX = x - (x % Tabla.squareSize);
            piesa.dragY = y - (y % Tabla.squareSize);
            this.offsetX = x - piesa.col * Tabla.squareSize;
            this.offsetY = y - piesa.row * Tabla.squareSize;

            this.tabla.redesenare(this.tabla.piese.filter(p => p !== piesa));
        }
    }

    onMouseMove(e) {
        const {col, row, x, y } = this.getSquareFromMouse(e);
        const piesa = this.tabla.getPiesa(row, col);
        if(piesa && piesa.color == this.culoareCurenta)
            this.canvas.style.cursor = "grab";
        else
            this.canvas.style.cursor = "default";
        if (!this.piesaSelectata) return;

        this.canvas.style.cursor = "grab";
        this.piesaSelectata.dragX = x - this.offsetX;
        this.piesaSelectata.dragY = y - this.offsetY;

        this.tabla.redesenare(this.tabla.piese.filter(p => p !== this.piesaSelectata));
        this.piesaSelectata.desen(this.tabla.ctx, this.piesaSelectata.img, this.piesaSelectata.dragX, this.piesaSelectata.dragY);
    }

    async onMouseUp(e) {
        if (!this.piesaSelectata) return;

        const { col, row } = this.getSquareFromMouse(e);

        const moveData = {
            fromRow: this.piesaSelectata.row,
            fromCol: this.piesaSelectata.col,
            toRow: row,
            toCol: col
        };

        let moveResult = null;

        try {
            const resp = await fetch(
                `/api/chess/move?fromRow=${moveData.fromRow}&fromCol=${moveData.fromCol}&toRow=${moveData.toRow}&toCol=${moveData.toCol}`,
                { method: "POST" }
            );

            console.log("from:", moveData.fromRow, moveData.fromCol);
            console.log("to:", moveData.toRow, moveData.toCol);

            if (!resp.ok) {
                console.error("Eroare de rețea:", resp.statusText);
                return;
            }

            moveResult = await resp.json();

            if (!moveResult.success) {
                console.log("Mutare invalidă:", moveResult.message);
                return;
            }

            this.tabla.setPiecesFromServer(moveResult.updatedPieces);
            this.culoareCurenta = moveResult.culoareCurenta;

            const statusDiv = document.getElementById("status");
            if (statusDiv) {
                statusDiv.innerText =
                    "Culoarea curenta muta: " +
                    (this.culoareCurenta === 1 ? "Alb" : "Negru");
            }
            const destination = document.getElementById("movesPGN");
            if (destination) {
                destination.innerText = moveResult.pgn;
            }

            if(moveResult.checkmate === true){
                this.winner = this.piesaSelectata.color;
                console.log("Culoarea castigatoare este " + this.convertNumberToSide(this.winner));
            }
            if (moveResult.check === true) { // naming conventions in spring boot, field isCheck -> check
                this.soundManager.play("check");
                if(moveResult.checkmate === true)
                    this.soundManager.play("end");
            } else if(moveResult.captures === true) {
                this.soundManager.play("capture");
            }
            else{
                this.soundManager.play("move");
            }

        } catch (err) {
            console.error("Eroare mutare:", err);

        } finally {
            if (this.piesaSelectata) {
                this.piesaSelectata.isDragging = false;
                this.piesaSelectata.dragX = undefined;
                this.piesaSelectata.dragY = undefined;
            }

            this.piesaSelectata = null;
            this.tabla.redesenare();
            this.canvas.style.cursor = "default";
        }
    }

}
