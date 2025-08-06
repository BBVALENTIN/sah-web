import {Tabla} from "./tabla.js";

export class MutareMouse {
    constructor(canvas, tabla) {
        this.canvas = canvas;
        this.piesaSelectata = null;
        this.culoareCurenta = "alb";
        this.offsetX = 0;
        this.offsetY = 0;

        this.tabla = tabla;

        this.canvas.addEventListener("mousedown", this.onMouseDown.bind(this));
        this.canvas.addEventListener("mousemove", this.onMouseMove.bind(this));
        this.canvas.addEventListener("mouseup", this.onMouseUp.bind(this));
    }

    getMouseCoords(event) {
        const rect = this.canvas.getBoundingClientRect();
        return {
            x: event.clientX - rect.left,
            y: event.clientY - rect.top
        };
    }

    onMouseDown(event) {
        if (this.piesaSelectata === null) {
            const {x, y} = this.getMouseCoords(event);
            this.copie(this.tabla.piese, this.tabla.simPiese);
            console.log("Mouse down:", x, y);
            const col = Math.floor(x / Tabla.squareSize);
            const row = Math.floor(y / Tabla.squareSize);
            for (const piesa of this.tabla.simPiese) {
                if (piesa.row === row && piesa.col === col && piesa.color === this.culoareCurenta) {
                    this.piesaSelectata = piesa;
                    this.piesaSelectata.prerow = piesa.row;
                    this.piesaSelectata.precol = piesa.col;
                    piesa.isDragging = true;

                    this.offsetX = Tabla.squareSize / 2;
                    this.offsetY = Tabla.squareSize / 2;

                    break;
                }
            }
        }
    }


    onMouseMove(event) {
        if (this.piesaSelectata && this.piesaSelectata.isDragging) {
            const { x, y } = this.getMouseCoords(event);
            console.log("Mouse move:", x, y);

            this.piesaSelectata.dragX = x - this.offsetX;
            this.piesaSelectata.dragY = y - this.offsetY;

            this.tabla.redesenare(this.tabla.simPiese);
        }
    }


    onMouseUp(event) {
        if (this.piesaSelectata && this.piesaSelectata.isDragging) {
            if(this.piesaSelectata !== null)
            {
                this.simulate(event);
                if(this.validSquare === true)
                {
                    this.copie(this.tabla.simPiese, this.tabla.piese);
                    const piesaReala = this.gasesteInPiese(this.piesaSelectata);
                    if (piesaReala) {
                        piesaReala.row = this.piesaSelectata.row;
                        piesaReala.col = this.piesaSelectata.col;
                        piesaReala.updatePozitie();
                    }
                    this.schimbaJucatorul();
                    console.log(this.culoareCurenta);
                    console.log("x", this.piesaSelectata.x, "y", this.piesaSelectata.y);
                }
                else
                {
                    this.copie(this.tabla.piese, this.tabla.simPiese);
                    this.piesaSelectata.resetPozitie();
                }
            }

            this.piesaSelectata.isDragging = false;

            this.piesaSelectata.dragX = undefined;
            this.piesaSelectata.dragY = undefined;

            this.piesaSelectata = null;
            this.tabla.redesenare(this.tabla.piese);
        }
    }

    gasesteInPiese(piesaSim) {
        return this.tabla.piese.find(p =>
            p.row === piesaSim.prerow &&
            p.col === piesaSim.precol &&
            p.color === piesaSim.color &&
            p.constructor.name === piesaSim.constructor.name
        );
    }


    copie(sourceArray, destinationArray) {
        destinationArray.length = 0;
        for (const obiect of sourceArray) {
            const clone = Object.assign(Object.create(Object.getPrototypeOf(obiect)), obiect);
            clone.tabla = this.tabla;
            destinationArray.push(clone);
        }
    }

    getPiesaSelectata()
    {
        return this.piesaSelectata;
    }

    schimbaJucatorul()
    {
        if(this.culoareCurenta === "alb")
            this.culoareCurenta = "negru";
        else
            this.culoareCurenta = "alb";
    }
    simulate(event) {
        const { x, y } = this.getMouseCoords(event);

        this.copie(this.tabla.piese, this.tabla.simPiese);
        this.tabla.simPiese.forEach((piesa, i) => {
            console.log(`Piesa ${i}:`, piesa);
        });

        this.piesaSelectata.col = Math.floor(x / Tabla.squareSize);
        this.piesaSelectata.row = Math.floor(y / Tabla.squareSize);

        console.log("Col:", this.piesaSelectata.col, "Row:", this.piesaSelectata.row);
        console.log("mutariLegale?", this.piesaSelectata.mutariLegale(this.piesaSelectata.col, this.piesaSelectata.row));
        if(this.piesaSelectata.mutariLegale(this.piesaSelectata.col, this.piesaSelectata.row)){

            if(this.piesaSelectata.lovestePiese !== null)
            {
                let index = -1;
                for (let i = 0; i < this.tabla.simPiese.length; i++) {
                    const piesa = this.tabla.simPiese[i];

                    if (piesa.row === this.piesaSelectata.lovestePiese.row &&
                        piesa.col === this.piesaSelectata.lovestePiese.col &&
                        piesa.color === this.piesaSelectata.lovestePiese.color &&
                        piesa.constructor.name === this.piesaSelectata.lovestePiese.constructor.name) {
                        index = i;
                        break;
                    }
                }

                if (index !== -1) {
                    this.tabla.simPiese.splice(index, 1);
                    console.log("Piesa a fost ștearsă!");
                } else {
                    console.log("Nu s-a găsit piesa de șters!");
                }
            }
            this.canMove = true;
            this.validSquare = true;
        } else {
            this.validSquare = false;
        }
    }
    getIndexPiesaLa(row, col) {
        for (let i = 0; i < this.tabla.simPiese.length; i++) {
            const piesa = this.tabla.simPiese[i];
            if (piesa.row === row && piesa.col === col) {
                return i;
            }
        }
        return -1;
    }

}
