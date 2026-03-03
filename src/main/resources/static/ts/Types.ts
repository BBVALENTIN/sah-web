import {MessageType, LobbyType, Formats} from "./Enums.js";

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
    lastMove: LastMove;
}

interface LastMove {
    fromRow: number;
    fromCol: number;
    toRow: number;
    toCol: number;
}

export interface Mutare_Error {
    error: "PIESA_NEDETECTATA" | "RAND_GRESIT" | "MUTARE_ILEGALA"
}
export interface MoveRow {
    number: number;
    white?: string;
    black?: string;
}

export interface userInfo {
    userId: number;
    username: string;
}

export interface lobbyInfo {
    lobbyId: string;
    lobbyType: LobbyType;
    loggedUsername: userInfo;
    playerWhite: string;
    playerBlack: string;
}

export interface Message {
    sender: string;
    content: string;
    type: MessageType;
}