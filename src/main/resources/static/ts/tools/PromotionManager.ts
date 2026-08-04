import {Piece} from "./Piece.js";
import {PieceType, SidesExplicit} from "../tools/Enums.js";

export interface PendingMove {
    fromRow: number;
    fromCol: number;
    targetRow: number;
    targetCol: number;
}

export class PromotionManager {
    private readonly promotionModal: HTMLDivElement;
    private readonly promotionChoices: HTMLDivElement;
    private pendingMove: PendingMove | null = null;

    constructor(modalId: string = 'promotion-modal', choicesId: string = 'promotion-choices') {
        this.promotionModal = document.getElementById(modalId) as HTMLDivElement;
        this.promotionChoices = document.getElementById(choicesId) as HTMLDivElement;
    }

    isPromotionMove(piece: Piece, fromRow: number, targetRow: number): boolean {
        if (piece.tip !== PieceType.PAWN) return false;
        if (piece.color === SidesExplicit.WHITE && (fromRow === 1 && targetRow === 0)) return true;
        if (piece.color === SidesExplicit.BLACK && (fromRow === 6 && targetRow === 7)) return true;
        return false;
    }

    showPromotionModal(color: SidesExplicit, position: { x:number, y: number}, callback: (piece: string) => void): void {
        const pieces = color === SidesExplicit.WHITE ?
            ['white-queen', 'white-rook', 'white-bishop', 'white-knight'] :
            ['black-queen', 'black-rook', 'black-bishop', 'black-knight'];

        const chars = ['q', 'r', 'b', 'n'];

        this.promotionChoices.innerHTML = '';
        pieces.forEach((imgName, idx) => {
            const btn = document.createElement('div');

            btn.className = 'promotion-piece-btn';
            btn.innerHTML = `<img src="images/pieces-default/${imgName}.png" alt="${chars[idx]}">`;
            btn.onclick = () => {
                this.hidePromotionModal();
                callback(chars[idx]);
            };
            this.promotionChoices.appendChild(btn);
        });
        this.promotionModal.style.display = 'flex';

        this.promotionModal.style.position = 'fixed';
        this.promotionModal.style.zIndex = '9999';
        this.promotionModal.style.left = `${position.x}px`;
        this.promotionModal.style.top = `${position.y}px`;
        this.promotionModal.style.transform = 'translate(-50%)';
        this.promotionModal.style.display = 'flex';
    }

    hidePromotionModal(): void {
        this.promotionModal.style.display = 'none';
    }

    setPendingMove(move: PendingMove): void {
        this.pendingMove = move;
    }

    getPendingMove(): PendingMove | null {
        return this.pendingMove;
    }

    clearPendingMove(): void {
        this.pendingMove = null;
    }
}