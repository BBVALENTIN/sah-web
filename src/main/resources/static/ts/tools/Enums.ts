export enum TipPiesa {
    PAWN = 'PAWN',
    ROOK = 'ROOK',
    KNIGHT = 'KNIGHT',
    BISHOP = 'BISHOP',
    QUEEN = 'QUEEN',
    KING = 'KING'
}

export enum culoriPiesa {
    ALB = "ALB",
    NEGRU = "NEGRU",
    OVER = "OVER"
}

export enum MessageType {
    CHAT = 'CHAT',
    JOIN = 'JOIN',
    LEAVE = 'LEAVE'
}

export enum LobbyType {
    AVAILABLE = 'AVAILABLE',
    ONGOING = 'ONGOING',
    FINISHED = 'FINISHED',
    ABORTED = 'ABORTED'
}

export enum Formats {
    BLITZ = 'BLITZ',
    CLASSICAL = 'CLASSICAL'
}

export enum WinReason {
    CHECKMATE = 'CHECKMATE',
    RESIGNATION = 'RESIGNATION',
    ABANDONMENT = 'ABANDONMNET'
}

export enum Sides {
    WHITE,
    BLACK,
    NONE
}

export enum SidesExplicit {
    WHITE = "WHITE",
    BLACK = "BLACK"
}

export enum moveSounds {
    normalMove = "move",
    check = "check",
    checkmate = "checkmate",
    capture = "capture",
    end = "end"
}