import {Board} from "./Board.js"
import {MoveList} from "./MoveList.js"

export interface AppCore {
    board: Board,
    moveList: MoveList,
}

export function initCanvas(canvasId: string, moveListId: string): AppCore {
    const board = new Board(canvasId);
    const moveList = new MoveList(moveListId);

    return { board, moveList };
}

export async function initBoard(board: Board, isBlack = false): Promise<void> {
    await board.loadImages();
    board.setOrientation(isBlack);
    board.redraw();
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