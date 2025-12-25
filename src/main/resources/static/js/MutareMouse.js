import { Tabla } from "./tabla.js";

export class MutareMouse {
    constructor(canvas, tabla) {
        this.canvas = canvas;
        this.tabla = tabla;
        this.piesaSelectata = null;
        this.offsetX = 0;
        this.offsetY = 0;
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

    async getRegi()
    {
        let regeInamic = null, regeTau = null;
        const respInamic = await fetch(`api/chess/regeInamic`);
        const respTau = await fetch(`api/chess/regeTau`);
        if(respInamic.ok && respTau.ok) {
            regeInamic = await respInamic.json();
            regeTau = await respTau.json();
        }
        console.log(regeInamic);
        console.log(regeTau);
    }

    async getCheck()
    {
        const respSAH = await fetch(`api/chess/check`);
        const respSAHMAT = await fetch(`api/chess/checkmate`);
        let esteSahJSON = null, esteSAHMATJSON = null;
        if(respSAH.ok && respSAHMAT.ok)
        {
            esteSahJSON = await respSAH.json();
            esteSAHMATJSON = await respSAHMAT.json();
        }
        console.log("Sah: ", esteSahJSON, "Sah-Mat: ", esteSAHMATJSON);
    }

    async getMovesPGN()
    {
        const respPGN = await fetch(`/api/chess/getMovesPGN`)
        let movesPGN = null;
        if(respPGN.ok)
        {
            movesPGN = await respPGN.text();
        }
        const destination = document.getElementById("movesPGN");
        destination.innerText = movesPGN;
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
        await this.getTurn();
        await this.getRegi();
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
        if (!this.piesaSelectata) return;
        const { x, y } = this.getSquareFromMouse(e);

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
                    this.tabla.setPiecesFromServer(moveResult.updatedPieces);
                    await this.getTurn();
                    await this.getCheck();
                    await this.getMovesPGN();
                    const statusDiv = document.getElementById("status");
                    statusDiv.innerText = "Culoarea curenta muta: " + (this.culoareCurenta === 1 ? "Alb" : "Negru");
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
