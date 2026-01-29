export interface PiesaDTO {
    tip: String;
    color: number;
    row: number;
    col: number;
}

export type Mutare = Mutare_Reusita | Mutare_Error;


export interface Mutare_Reusita {
    updatedPieces: PiesaDTO[];
    check: boolean;
    checkmate: boolean;
    culoareCurenta: 1 | 0 | -1;
    pgn: string;
    captures: boolean;
}

export interface Mutare_Error {
    error: "PIESA_NEDETECTATA" | "RAND_GRESIT" | "MUTARE_ILEGALA"
}
export interface MoveRow {
    number: number;
    white?: string;
    black?: string;
}