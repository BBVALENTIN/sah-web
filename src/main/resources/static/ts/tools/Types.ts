import {MessageType, LobbyType, Formats, Sides, SidesExplicit, PieceType} from "./Enums.js";

export interface PieceDTO {
    Type: String;
    color: Sides;
    row: number;
    col: number;
}

export interface Mutare_Reusita {
    updatedPieces: PieceDTO[];
    check: boolean;
    checkmate: boolean;
    currentColor: SidesExplicit;
    errorCodes: string;
    pgn: string;
    captures: boolean;
    lastMove: LastMove;
    fen: string;
    capturedPieces: PieceDTO[];
}

export interface OptimisedMove {
    fen: string;
    pgn: string;
    moveCoords: LastMove;
    isCheck: boolean;
    isCheckMate: boolean;
    capturedPieces: minPiece;
    currentColor: SidesExplicit;
}

interface minPiece {
    type: string;
    color: Sides;
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
    Pieces: PieceDTO[];
    currentColor: Sides;
    currentPGN: string;
}