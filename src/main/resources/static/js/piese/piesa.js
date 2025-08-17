import { Tabla } from "../tabla.js";

export class Piesa {
    constructor(color, row, col, tabla) {
        this.color = color;
        this.row = row;
        this.col = col;
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
}
