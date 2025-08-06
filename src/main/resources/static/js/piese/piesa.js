import { Tabla } from "../tabla.js";

export class Piesa {
    constructor(color, row, col, tabla) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.prerow = row;
        this.precol = col;
        this.miscata = false;
        this.lovestePiese = null;
        this.tabla = tabla;
        this.x = this.getX(col);
        this.y = this.getY(row);
    }



    desen(ctx, imagine) {
        if (!imagine) return;

        let xPixeli, yPixeli;

        if (this.isDragging && this.dragX !== undefined && this.dragY !== undefined) {
            xPixeli = this.dragX;
            yPixeli = this.dragY;
        } else {
            xPixeli = this.col*Tabla.squareSize;
            yPixeli = this.row*Tabla.squareSize;
        }

        if (imagine.complete) {
            ctx.drawImage(imagine, xPixeli, yPixeli, Tabla.squareSize, Tabla.squareSize);
        } else {
            imagine.onload = () => {
                ctx.drawImage(imagine, xPixeli, yPixeli, Tabla.squareSize, Tabla.squareSize);
            };
        }
    }


    getCol(x) {
        return Math.floor(x / Tabla.squareSize);
    }

    getRow(y) {
        return Math.floor(y / Tabla.squareSize);
    }

    getX(col) {
        return col*Tabla.squareSize;
    }

    getY(row) {
        return row * Tabla.squareSize;
    }


    getLovesteP(targetCol, targetRow)
    {
        for(const piesa of this.tabla.piese)
        {
            if(piesa.col === targetCol && piesa.row === targetRow && piesa !== this)
                return piesa;
        }
        return null;
    }


    patratValid(targetCol, targetRow)
    {
        this.lovestePiese = this.getLovesteP(targetCol, targetRow);
        if(this.lovestePiese === null)
            return true;
        if(this.lovestePiese.color !== this.color)
            return true;
        this.lovestePiese = null;
        return false;
    }

    peTabla(targetCol, targetRow)
    {
        if(targetRow >= 0 && targetRow <= 7 && targetCol >=0 && targetCol <= 7)
            return true;
        return false;
    }

    acelasiPatrat(targetCol, targetRow) {
        return targetCol === this.precol && targetRow === this.prerow;
    }


    resetPozitie()
    {
        this.col = this.precol;
        this.row = this.prerow;
        this.x = this.getX(this.col);
        this.y = this.getY(this.row)
    }

    updatePozitie() {
        this.x = this.col * Tabla.squareSize;
        this.y = this.row * Tabla.squareSize;

        this.precol = this.col;
        this.prerow = this.row;
        this.miscata = true;

        this.dragX = undefined;
        this.dragY = undefined;
    }

    piesaInFata(targetCol, targetRow) {
        if (this.prerow === targetRow) {
            const start = Math.min(this.precol, targetCol) + 1;
            const end = Math.max(this.precol, targetCol);

            for (let col = start; col < end; col++) {
                for (const piesa of this.tabla.piese) {
                    if (piesa.col === col && piesa.row === targetRow) {
                        this.lovestePiese = piesa;
                        return true;
                    }
                }
            }
        }

        if (this.precol === targetCol) {
            const start = Math.min(this.prerow, targetRow) + 1;
            const end = Math.max(this.prerow, targetRow);

            for (let row = start; row < end; row++) {
                for (const piesa of this.tabla.piese) {
                    if (piesa.col === targetCol && piesa.row === row) {
                        this.lovestePiese = piesa;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    piesePeDiagonala(targetCol, targetRow)
    {
        if(targetRow < this.prerow)
        {
            for(let i = this.precol-1; i > targetCol; i--)
            {
                let dif = Math.abs(i - this.precol);
                for(const piesa of this.tabla.piese)
                    if(piesa.col === i && piesa.row === this.prerow - dif){
                        this.lovestePiese = piesa;
                        return true;
                    }
            }

            for(let i = this.precol+1; i < targetCol; i++)
            {
                let dif = Math.abs(i - this.precol);
                for(const piesa of this.tabla.piese)
                    if(piesa.col === i && piesa.row === this.prerow - dif){
                        this.lovestePiese = piesa;
                        return true;
                    }
            }
        }

        if(targetRow > this.prerow)
        {
            for(let i = this.precol-1; i > targetCol; i--)
            {
                let dif = Math.abs(i-this.precol);
                for(const piesa of this.tabla.piese)
                    if(piesa.col === i && piesa.row === this.prerow + dif){
                        this.lovestePiese = piesa;
                        return true;
                    }
            }

            for(let i = this.precol+1; i < targetCol; i++)
            {
                let dif = Math.abs(i - this.precol);
                for(const piesa of this.tabla.piese)
                    if(piesa.col === i && piesa.row === this.prerow + dif){
                        this.lovestePiese = piesa;
                        return true;
                    }
            }
        }

        return false;
    }

    mutariLegale(targetCol, targetRow) {
        return false;
    }
}
