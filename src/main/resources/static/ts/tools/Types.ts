import {MessageType, LobbyType, Formats, Sides, SidesExplicit, PieceType} from "./Enums.js";
import {Piece} from "../pieces/Piece.js";

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
    lastMove: MoveCoords;
    fen: string;
    capturedPieces: PieceDTO[];
}

export interface OptimisedMove {
    fen: string;
    pgn: string;
    lastMoveCoords: MoveCoords;
    check: boolean;
    checkMate: boolean;
    capturedPieces: minPiece;
    currentColor: SidesExplicit;
}

interface minPiece {
    type: string;
    color: Sides;
}

export interface mvData {
    fromRow: number;
    fromCol: number;
    targetRow: number;
    targetCol: number;
    promotionPiece: null | string;
}

export interface MoveCoords {
    fromRow: number;
    fromCol: number;
    targetRow: number;
    targetCol: number;
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
    currentFEN: string;
    currentColor: Sides;
    currentPGN: string;
}