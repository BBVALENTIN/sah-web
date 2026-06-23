import {MoveRow} from "./Types.js";
import {Sides} from "./Enums.js";

export class MoveList {
    private container: HTMLElement;
    private rows: MoveRow[] = [];
    private moveCount: number = 0; // contor pentru alb/negru
    private lastPGN: string = "";

    constructor(containerId: string) {
        const el = document.getElementById(containerId);
        if (!el) throw new Error("Container not found: " + containerId);
        this.container = el;
    }

    addMove(pgn: string) {
        if (!pgn) return;
        this.lastPGN = pgn;

        const color: "white" | "black" = this.moveCount % 2 === 0 ? "white" : "black";

        if (color === "white") {
            const row: MoveRow = {
                number: Math.floor(this.moveCount / 2) + 1,
                white: pgn
            };
            this.rows.push(row);
        } else {
            const lastRow = this.rows[this.rows.length - 1];
            if (lastRow) lastRow.black = pgn;
        }

        this.moveCount++;
        this.render();
    }

    private render() {
        this.container.innerHTML = "";

        for (const row of this.rows) {
            this.container.append(
                this.createCell("move-number", row.number + "."),
                this.createCell("move", row.white ?? ""),
                this.createCell("move", row.black ?? "")
            );
        }

        this.container.scrollTop = this.container.scrollHeight;
    }

    public addWholePGN(PGN: string) {
        const result: string[] = PGN.split(" ");
        for(let i = 0; i <= result.length; i++) {
            this.addMove(result[i]);
        }
    }

    private createCell(className: string, text: string) {
        const div = document.createElement("div");
        div.className = className;
        div.textContent = text;
        return div;
    }

    reset() {
        this.rows = [];
        this.moveCount = 0;
        this.lastPGN = "";
        this.container.innerHTML = "";
        this.resetCopyables();
    }

    resetCopyables() {
        const cpbPGN = document.getElementById('PGN') as HTMLTextAreaElement;
        const cpbFEN = document.getElementById('FEN') as HTMLInputElement;

        console.log(cpbFEN, cpbPGN);
        if(!cpbPGN) return;
        if(!cpbFEN) return;

        cpbPGN.value = '';

        cpbFEN.value = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'; //starting FEN
    }

    public getMoveCount() {
        return this.moveCount;
    }
}