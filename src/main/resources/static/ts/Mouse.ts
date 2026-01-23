import { Tabla } from "./Tabla.js";
import { SoundManager } from "./audio/soundManager.js";
import { Piesa} from "./piese/Piesa.js";
import {Mutari} from "./Types.js";

export class Mouse {
    canvas: HTMLCanvasElement;
    tabla: Tabla;
    piesaSelectata: Piesa | undefined;
    soundManager: SoundManager = new SoundManager();
    offsetX: number;
    offsetY: number;
    winner: 1 | 0 | -1 | undefined;
    culoareCurenta: 1 | 0 | -1 | undefined;
    constructor(canvas: HTMLCanvasElement, tabla: Tabla) {
        this.canvas = canvas;
        this.tabla = tabla;
        this.piesaSelectata = undefined;
        this.offsetX = 0;
        this.offsetY = 0;
        this.winner = undefined;

        this.canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        this.canvas.addEventListener("mousemove", this.MouseMove.bind(this));
        this.canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    getSquareFromMouse(e: any){
        const rect: DOMRect = this.canvas.getBoundingClientRect();
        const x: number = e.clientX - rect.left;
        const y: number = e.clientY - rect.top;
        const col: number = Math.floor(x / Tabla.squareSize);
        const row: number = Math.floor(y / Tabla.squareSize);

        return { col, row, x, y};
    }

    async getTurn() {
        const resp: Response = await fetch(`api/chess/turn`);
        if(resp.ok)
            this.culoareCurenta = await resp.json();
    }

    public onMouseDown(e:any):void {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piesa = this.tabla.getPiesa(row, col);
        if(piesa && piesa.color === this.culoareCurenta)
        {
            this.piesaSelectata = piesa;
            this.piesaSelectata.isDragging = true;
            piesa.dragX = x - (x % Tabla.squareSize);
            piesa.dragY = y - ( y % Tabla.squareSize);
            this.offsetX = x - piesa.col * Tabla.squareSize;
            this.offsetY = y - piesa.row * Tabla.squareSize;

            this.tabla.redesenare((this.tabla.piese.filter(p => p!== piesa)));
        }
    }
    public async MouseMove(e:any):Promise<void> {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piesa = this.tabla.getPiesa(row, col);
        if(piesa && piesa.color == this.culoareCurenta)
            this.canvas.style.cursor = "grab";
        else
            this.canvas.style.cursor = "default";
        if (!this.piesaSelectata) return;

        this.canvas.style.cursor = "grabbing";
        this.piesaSelectata.dragX = x - this.offsetX;
        this.piesaSelectata.dragY = y - this.offsetY;

        this.tabla.redesenare(this.tabla.piese, this.piesaSelectata);
    }
    public async onMouseUp(e: any): Promise<void> {
        if(!this.piesaSelectata) { return; }
        const { col, row } = this.getSquareFromMouse(e);

        const moveData = {
            fromRow: this.piesaSelectata.row,
            fromCol: this.piesaSelectata.col,
            toRow: row,
            toCol: col
        };

        try {
            const respMutari = await fetch(`/api/chess/move?fromRow=${moveData.fromRow}&fromCol=${moveData.fromCol}&toRow=${moveData.toRow}&toCol=${moveData.toCol}`, {method: "POST"});

            if(!respMutari.ok){
                console.error("Eroare la JSON: ", respMutari.statusText);
                return;
            }

            console.log("from: ", moveData.fromRow, moveData.fromCol);
            console.log("to: ", moveData.toRow, moveData.toCol);

            let moveResult: Mutari = await respMutari.json()

            if(!moveResult.success) {
                console.log("Mutare invalida: ", moveResult.message);
                return;
            }

            this.tabla.setPiecesFromServer(moveResult.updatedPieces);
            this.culoareCurenta = moveResult.culoareCurenta;
        } catch(err){
            console.log("eroare cine stie de ce");
        } finally {
            if(this.piesaSelectata) {
                this.piesaSelectata.isDragging = false;
                this.piesaSelectata.dragX = undefined;
                this.piesaSelectata.dragY = undefined;
            }

            this.piesaSelectata = undefined;
            this.tabla.redesenare();
            this.canvas.style.cursor = "default";
        }
    }
}