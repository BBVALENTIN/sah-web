import {Tabla} from "./Tabla.js"
import {MoveList} from "./MoveList.js"

export interface AppCore {
    tabla: Tabla,
    moveList: MoveList,
    canvas: HTMLCanvasElement,
    ctx: CanvasRenderingContext2D
};

export function initCanvas(canvasId: string, moveListId: string): AppCore {
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    const ctx = canvas.getContext('2d')!;

    const size = Tabla.squareSize * 8;
    canvas.width = size;
    canvas.height = size;

    const tabla = new Tabla(ctx);
    const moveList = new MoveList(moveListId);

    return { tabla, moveList, canvas, ctx };
}

export async function initTabla(tabla: Tabla, isBlack = false): Promise<void> {
    await tabla.loadImages();
    tabla.setOrientare(isBlack);
    tabla.redesenare();
}

export async function getLoggedUsername(): Promise<string> {
    try {
        const response = await fetch('/info/user');
        if (response.ok) {
            const data = await response.json();
            return data.username;
        }
    } catch (e) {
        console.error('Failed to fetch user info', e);
    }
    return "";
}

export function onButtonClick(
    buttonId: string,
    handler: (e: MouseEvent) => void
): void {
    const btn = document.getElementById(buttonId) as HTMLButtonElement | null;
    btn?.addEventListener('click', handler);
}