import {MessageType, LobbyType, Formats, culoriPiesa} from "./Enums.js";

export interface PiesaDTO {
    tip: String;
    color: culoriPiesa;
    row: number;
    col: number;
}

export interface Mutare_Reusita {
    updatedPieces: PiesaDTO[];
    check: boolean;
    checkmate: boolean;
    culoareCurenta: culoriPiesa;
    errorCodes: string;
    pgn: string;
    captures: boolean;
    lastMove: LastMove;
    fen: string; // FUCK JACKSON
}

interface LastMove {
    fromRow: number;
    fromCol: number;
    toRow: number;
    toCol: number;
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

export interface minimalState {
    Piese: any;
    culoareCurenta: culoriPiesa;
    currentPGN: string;
}