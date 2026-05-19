import {Tabla} from "../Tabla.js";
import {Piesa} from "../piese/Piesa.js";
import {SoundManager} from "../audio/soundManager.js";
import {culoriPiesa} from "../Enums.js";
import {Mutare_Reusita} from "../Types.js";
import {moveList, cereMutareDeLaStockfish, engineOn} from "./index.js";

const FENdiv = document.getElementById('FEN') as HTMLDivElement;
export let FEN: string;
export class MousePractice {
    canvas: HTMLCanvasElement;
    tabla: Tabla;
    piesaSelectata: Piesa | undefined;
    soundManager: SoundManager = new SoundManager();
    offsetX: number;
    offsetY: number;
    culoareCurenta: culoriPiesa;
    constructor(canvas: HTMLCanvasElement, tabla: Tabla) {
        this.canvas = canvas;
        this.tabla = tabla;
        this.piesaSelectata = undefined;
        this.offsetX = 0;
        this.offsetY = 0;
        this.culoareCurenta = culoriPiesa.ALB;
        this.canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        this.canvas.addEventListener("mousemove", this.MouseMove.bind(this));
        this.canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    async handleMutareAPI(e: any):Promise<Mutare_Reusita> {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        let mutare: Promise<Mutare_Reusita>;
        let errorText: any;
        const moveData = {
            fromRow: this.piesaSelectata!.row,
            fromCol: this.piesaSelectata!.col,
            toRow: row,
            toCol: col
        };
        const respMutare: Response = await fetch(`/api/chess/move?fromRow=${moveData.fromRow}&fromCol=${moveData.fromCol}&toRow=${moveData.toRow}&toCol=${moveData.toCol}`, {method: "POST"});
        if(!respMutare.ok) {
            const errorText = await respMutare.text();
            this.reset();
            throw new Error(errorText);
        }
        mutare = await respMutare.json();
        return mutare;
    }

    getSquareFromMouse(e: any){
        const rect: DOMRect = this.canvas.getBoundingClientRect();
        const x: number = e.clientX - rect.left;
        const y: number = e.clientY - rect.top;

        let col: number = Math.floor(x / Tabla.squareSize);
        let row: number = Math.floor(y / Tabla.squareSize);

        if(this.tabla.isBlack) {
            col = 7 - col;
            row = 7 - row;
        }
        return { col, row, x, y};
    }



    public async onMouseDown(e:any) {
        const { col, row, x, y} = this.getSquareFromMouse(e);
        const piesa = this.tabla.getPiesa(row, col);
        if (piesa && piesa.color === this.culoareCurenta) {
            this.piesaSelectata = piesa;
            this.piesaSelectata.isDragging = true;

            const vizualCol = this.tabla.isBlack ? 7 - piesa.col : piesa.col;
            const vizualRow = this.tabla.isBlack ? 7 - piesa.row : piesa.row;

            this.offsetX = x - vizualCol * Tabla.squareSize;
            this.offsetY = y - vizualRow * Tabla.squareSize;

            this.tabla.redesenare(this.tabla.piese.filter(p => p !== piesa));
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

        this.tabla.setLastMove(this.piesaSelectata.row, this.piesaSelectata.col, row, col);

        try {
            const result:Mutare_Reusita | undefined = await this.handleMutareAPI(e);
            console.log(result);
            if(result === undefined)
                return;
            if(result.checkmate){
                this.soundManager.play("checkmate");
                this.soundManager.play("end")
            }
            else if(result.check) { this.soundManager.play("check");}
            else if(result.captures) {this.soundManager.play("capture");}
            else {this.soundManager.play("move"); }

            moveList.addMove(result.pgn);
            this.tabla.setPiecesFromServer(result.updatedPieces);
            this.culoareCurenta = result.culoareCurenta;
            FEN = result.fen;
            if(engineOn)
                cereMutareDeLaStockfish(FEN);
            FENdiv.innerText = FEN;
        } catch(err){
            console.log("eroare cine stie de ce");
        } finally {
            this.reset();
        }
    }

    reset() {
        if(this.piesaSelectata) {
            this.piesaSelectata.isDragging = false;
            this.piesaSelectata.dragX = undefined;
            this.piesaSelectata.dragY = undefined;
        }

        this.piesaSelectata = undefined;

        this.canvas.style.cursor = "default";
        this.tabla.redesenare(this.tabla.piese);
    }
}