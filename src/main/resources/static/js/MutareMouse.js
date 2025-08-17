import { Tabla } from "./tabla.js";

export class MutareMouse {
    constructor(canvas, tabla) {
        this.canvas = canvas;
        this.tabla = tabla;
        this.piesaSelectata = null;
        this.culoareCurenta = 1; // 1 = alb, -1 = negru

        this.offsetX = 0;
        this.offsetY = 0;

        canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        canvas.addEventListener("mousemove", this.onMouseMove.bind(this));
        canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    getSquareFromMouse(e) {
        const rect = this.canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const col = Math.floor(x / Tabla.squareSize);
        const row = Math.floor(y / Tabla.squareSize);
        return { col, row, x, y };
    }

    onMouseDown(e) {
        const { col, row, x, y } = this.getSquareFromMouse(e);
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
        if (!this.piesaSelectata) return;
        const { x, y } = this.getSquareFromMouse(e);

        this.piesaSelectata.dragX = x - this.offsetX;
        this.piesaSelectata.dragY = y - this.offsetY;

        // Redesenăm tabla fără piesa selectată
        this.tabla.redesenare(this.tabla.piese.filter(p => p !== this.piesaSelectata));
        this.piesaSelectata.desen(this.tabla.ctx, this.piesaSelectata.img, this.piesaSelectata.dragX, this.piesaSelectata.dragY);
    }

    async onMouseUp(e) {
        if (!this.piesaSelectata) return;
        const { col, row } = this.getSquareFromMouse(e);

        // Pregătim datele mutării
        const moveData = {
            fromRow: this.piesaSelectata.row,
            fromCol: this.piesaSelectata.col,
            toRow: row,
            toCol: col
        };

        try {
            const resp = await fetch(
                `/api/chess/move?fromRow=${moveData.fromRow}&fromCol=${moveData.fromCol}&toRow=${moveData.toRow}&toCol=${moveData.toCol}`,
                { method: "POST" }
            );
            console.log("piesa e pe row si coloana:", moveData.fromRow, moveData.fromCol);
            console.log("row si coloana dorita:", moveData.toRow, moveData.toCol);

            if (resp.ok) {
                const moveResult = await resp.json();
                if (moveResult.success) {
                    // Setăm piesele din server
                    this.tabla.setPiecesFromServer(moveResult.updatedPieces);
                    this.culoareCurenta = this.culoareCurenta*-1;
                } else {
                    console.log("Mutare invalidă:", moveResult.message);
                }
            } else {
                console.error("Eroare de rețea:", resp.statusText);
            }
        } catch (err) {
            console.error("Eroare mutare:", err);
        } finally {
            this.piesaSelectata.isDragging = false;
            this.piesaSelectata.dragX = undefined;
            this.piesaSelectata.dragY = undefined;
            this.piesaSelectata = null;
            this.tabla.redesenare();
        }
    }
}
