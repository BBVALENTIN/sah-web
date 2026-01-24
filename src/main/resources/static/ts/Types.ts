export interface PiesaDTO {
    tip: String;
    color: number;
    row: number;
    col: number;
}

export interface Mutari {
    success: boolean;
    message: string;
    updatedPieces: PiesaDTO[];
    isCheck: boolean;
    isCheckmate: boolean;
    culoareCurenta: 1 | 0 | -1;
    pgn: string;
    captures: boolean;
}

export interface MoveRow {
    number: number;
    white?: string;
    black?: string;
}