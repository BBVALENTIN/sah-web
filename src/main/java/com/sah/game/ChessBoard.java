package com.sah.game;

import com.sah.dto.chess.*;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.ErrorCodes;
import com.sah.game.gameenums.CastlingNotation;
import com.sah.game.gameenums.Type;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.dtos.OCapturedPiece;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.pieces.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ChessBoard {
    public Pieces[][] board = new Pieces[8][8];
    public Pieces castling;
    public Pieces checkingPiece, selectedPiece, capturedPiece;
    public List<Pieces> piecesList = new ArrayList<>();
    public short movesPlayed, halfMove;
    public boolean promoted, isCheck, isCheckMate, resignation, canCastle;
    public CastlingInfoDTO castlingInfo;
    private LastMove lastMove;
    private MoveNotation moveNotation = new MoveNotation();
    private Sides winner;
    private List<OCapturedPiece> OCapturedPieces = new ArrayList<>();

    public ColorType currentColor = ColorType.WHITE;


    public ChessBoard() {
        initializeBoard();
    }

    public void initializeBoard() {
        this.board = new Pieces[8][8];

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(ColorType.BLACK, 1, i, this);
        }
        board[0][0] = new Rook(ColorType.BLACK, 0, 0, this);
        board[0][7] = new Rook(ColorType.BLACK, 0, 7, this);
        board[0][1] = new Knight(ColorType.BLACK, 0, 1, this);
        board[0][6] = new Knight(ColorType.BLACK, 0, 6, this);
        board[0][2] = new Bishop(ColorType.BLACK, 0, 2, this);
        board[0][5] = new Bishop(ColorType.BLACK, 0, 5, this);
        board[0][3] = new Queen(ColorType.BLACK, 0, 3, this);
        board[0][4] = new King(ColorType.BLACK, 0, 4, this);

        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(ColorType.WHITE, 6, i, this);
        }
        board[7][7] = new Rook(ColorType.WHITE, 7, 7, this);
        board[7][0] = new Rook(ColorType.WHITE, 7, 0, this);
        board[7][1] = new Knight(ColorType.WHITE, 7, 1, this);
        board[7][6] = new Knight(ColorType.WHITE, 7, 6, this);
        board[7][5] = new Bishop(ColorType.WHITE, 7, 5, this);
        board[7][2] = new Bishop(ColorType.WHITE, 7, 2, this);
        board[7][3] = new Queen(ColorType.WHITE, 7, 3, this);
        board[7][4] = new King(ColorType.WHITE, 7, 4, this);

        this.piecesList = getAllPieces();
        this.currentColor = ColorType.WHITE;
        this.isCheckMate = false;
        this.promoted = false;
        this.winner = null;
        this.movesPlayed = 1;
        this.moveNotation = new MoveNotation();
        this.castlingInfo = new CastlingInfoDTO();
    }

    public OMoveResult makeOptimisedMove(MoveCoords moveCoords) throws InvalidMoveException {
        short tr = moveCoords.getTargetRow();
        short tc = moveCoords.getTargetCol();
        short fromR = moveCoords.getFromRow();
        short fromC = moveCoords.getFromCol();
        selectedPiece = board[fromR][fromC];
        if(selectedPiece == null)
        {
             throw new InvalidMoveException(ErrorCodes.UNDETECTED_PIECE);
        }
        if(selectedPiece.color != currentColor)
        {
            throw new InvalidMoveException(ErrorCodes.WRONG_TURN);
        }

        boolean enPassant = false;

        if(selectedPiece.type == Type.PAWN && this.lastMove != null) {
            if(isEnPassant(selectedPiece, fromR, fromC, tr, tc) && canEnPassant(this.lastMove, selectedPiece, tr, tc)) {
                applyEnPassant(selectedPiece, tr, tc);
                enPassant = true;
            }
        }

        if(selectedPiece.move(tr, tc) == false && enPassant == false)
        {
            throw new InvalidMoveException(ErrorCodes.ILLEGAL_MOVE);
        }

        CastlingNotation castlingNotation = null;
        if(castling != null && canCastle) { // get the type of castle before so you dont copy an object, just a string
            Pieces castleCopy = castling;
            makeCastle(castling);
            castlingNotation = getCastlingType(castleCopy);
        } else if(castling != null && !canCastle)
        {
            throw new InvalidMoveException(ErrorCodes.ILLEGAL_MOVE);
        }

        capturedPiece = getCapturedPiece(tr, tc);
        boolean isCapture = false;
        if(capturedPiece != null) {
            isCapture = true;
            OCapturedPieces.add(toOCapturedPiece(capturedPiece));
        }

        List<Pieces> oldList = new ArrayList<>(piecesList);

        moveSelectedPiece(fromR, fromC, tr, tc, selectedPiece);
        if(selectedPiece.type == Type.PAWN)
            checkPromotion(selectedPiece, tr, tc);
        getAllPieces(); // maybe delete this

        if(isMyKingInCheck())
        {
            rollBack(tr, tc, fromR, fromC, selectedPiece); // change params order
            throw new InvalidMoveException(ErrorCodes.KING_IN_CHECK);
        }
        lastMove = new LastMove(fromR, fromC, tr, tc, toDTO(selectedPiece, tr, tc)); //ugly as HELL
        switchTurn();
        Pieces opponentKing = getKing(false);

        boolean isCheck = isKingInCheck(opponentKing);

        if(isCheck) {
            isCheckMate = isCheckmate(opponentKing);
        }

        MoveDataNotationDTO dto = assembleNotationDTO(moveCoords, oldList, castlingNotation, isCapture);

        String currentPGN = moveNotation.formatMove(dto);
        String currentFEN = moveNotation.generateFEN(new FENRequestDTO(board, currentColor, halfMove, castlingInfo)); // good enough

        promoted = false;
        if(isCheckMate) {
            winner = (currentColor == ColorType.WHITE) ? Sides.BLACK : Sides.WHITE;
        }

        return OMoveResult.builder()
                .fen(currentFEN)
                .pgn(currentPGN)
                .capturedPieceList(OCapturedPieces)
                .lastMoveCoords(moveCoords)
                .isCheck(isCheck)
                .isCheckMate(isCheckMate)
                .currentColor(currentColor)
                .build();
    }

    private MoveDataNotationDTO assembleNotationDTO(MoveCoords moveCoords, List<Pieces> oldList, CastlingNotation castlingNotation, boolean isCapture) {
        return MoveDataNotationDTO.builder()
                .fromRow(moveCoords.getFromRow())
                .fromCol(moveCoords.getFromCol())
                .targetRow(moveCoords.getTargetRow())
                .targetCol(moveCoords.getTargetCol())
                .oldPieces(oldList)
                .piece(selectedPiece) // should optimize to be only the types, no need for more
                .isCheck(isCheck)
                .isCheckMate(isCheckMate)
                .currentColor(currentColor)
                .castlingNotation(castlingNotation)
                .promoted(promoted)
                .isCapture(isCapture)
                .build();
    }

    private OCapturedPiece toOCapturedPiece(Pieces piece) {
        return new OCapturedPiece(piece.type, piece.color);
    }

    public synchronized List<Pieces> getAllPieces() {
        piecesList.clear();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null) {
                    piecesList.add(board[row][col]);
                }
            }
        }
        return piecesList;
    }

    private boolean isEnPassant(Pieces pawn, int fromRow, int fromCol, int targetRow, int targetCol) {
        if(pawn.type != Type.PAWN) return false;

        int dir = (pawn.color == ColorType.WHITE) ? -1 : 1;

        return Math.abs(fromCol-targetCol) == 1 && targetRow == fromRow + dir;
    }

    private boolean canEnPassant(LastMove lastMove, Pieces pawn, int targetRow, int targetCol) {
        if(lastMove == null || lastMove.getLastPiece().getType() != Type.PAWN) return false;

        boolean movedTwoSquares = Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == 2;

        return movedTwoSquares && lastMove.getToRow() == pawn.row && lastMove.getToCol() == targetCol;
    }

    private void applyEnPassant(Pieces pawn, int targetRow, int targetCol) {
        int dir = (pawn.color == ColorType.WHITE) ? 1 : -1;

        Pieces capturedPawn = board[targetRow + dir][targetCol];

        if(capturedPawn != null && capturedPawn.type == Type.PAWN) {
            board[targetRow+dir][targetCol] = null;
            OCapturedPieces.add(toOCapturedPiece(capturedPawn));
        }
    }
    public Pieces getCapturedPiece(int targetRow, int targetCol) {
        return board[targetRow][targetCol];
    }


    private void makeCastle(Pieces castling) {
        int rookOldRow = castling.row;
        int rookOldCol = castling.col;
        int rookNewCol = (castling.col == 7) ? 5 : 3;

        board[rookOldRow][rookNewCol] = castling;
        board[rookOldRow][rookOldCol] = null;
        castling.setPosition(rookOldRow, rookNewCol);

        this.castling = null;
    }

    private CastlingNotation getCastlingType(Pieces castling) {
        if(castling.precol == 0)
            return CastlingNotation.BIG;
        else
            return CastlingNotation.SMALL;
    }

    private void increaseHalfMove(Pieces selectedPiece, boolean isCapture) {
        if(selectedPiece.type == Type.PAWN || isCapture) {
            halfMove = 0;
        }
        else {
            halfMove++;
        }
    }

    private void moveSelectedPiece(int fromRow, int fromCol, int targetRow, int targetCol, Pieces selectedPiece)
    {
        board[fromRow][fromCol] = null;
        board[targetRow][targetCol] = selectedPiece;

        selectedPiece.setPosition(targetRow, targetCol);
    }

    private void rollBack(int targetRow, int targetCol, int fromRow, int fromCol, Pieces selectedPiece) {
        board[fromRow][fromCol] = selectedPiece;
        if(capturedPiece == null) {
            board[targetRow][targetCol] = null;
        }
        else {
            board[targetRow][targetCol] = capturedPiece;
        }
        selectedPiece.setPosition(fromRow, fromCol);
    }


    private void switchTurn() {
        if(currentColor == ColorType.WHITE)
            currentColor = ColorType.BLACK;
        else
            currentColor = ColorType.WHITE;
        movesPlayed++;
    }

    public ColorType getCurrentColor() {
        return currentColor;
    }


    public Pieces getKing(boolean opponent) {
        for (Pieces piece : piecesList) {
            if (piece.type == Type.KING) {
                if (opponent && piece.color != currentColor)
                    return piece;
                if (!opponent && piece.color == currentColor)
                    return piece;
            }
        }
        return null;
    }


    private boolean isMyKingInCheck() {
        Pieces king = getKing(false);
        return king != null && isKingInCheck(king);
    }

    public boolean isKingInCheck(Pieces king) {
        for (Pieces piece : piecesList) {
            if (piece.color != king.color) {
                if (piece.move(king.row, king.col)) {
                    checkingPiece = piece;
                    return true;
                }
            }
        }
        checkingPiece = null;
        return false;
    }

    public static PieceDTO toDTO(Pieces p, int r, int c) {
        return new PieceDTO(
                p.type,
                p.color,
                r,
                c
        );
    }

    public Pieces[][] getBoard() {
        return board;
    }

    public List<PieceDTO> getAllPiecesDTO() {
        List<PieceDTO> dto = new ArrayList<>();
        for(int r = 0; r < 8; r++) {
            for(int c = 0; c < 8; c++) {
                Pieces p = board[r][c];
                if(p != null) {
                    dto.add(toDTO(p, r, c));
                }
            }
        }
        return dto;
    }

    private boolean isCheckmate(Pieces king)
    {
        if(!isKingInCheck(king))
            return false;
        if(moveKing(king))
            return false;
        if(canSaveking(king))
            return false;
        return true;
    }

    private boolean moveKing(Pieces king){
        if(isValidMoveking(king, -1, -1)) return true;
        if(isValidMoveking(king, -1, -0)) return true;
        if(isValidMoveking(king, -1, 1)) return true;
        if(isValidMoveking(king, 0, -1)) return true;
        if(isValidMoveking(king, 0, 1)) return true;
        if(isValidMoveking(king, 1, -1)) return true;
        if(isValidMoveking(king, 1, 0)) return true;
        if(isValidMoveking(king, 1, 1)) return true;

        return false;
    }
    private boolean isValidMoveking(Pieces king, int rowSafe, int colSafe)
    {
        int newRow = king.row + rowSafe;
        int newCol = king.col + colSafe;

        if(!king.onTable(newRow, newCol)) return false;

        Pieces hitted = board[newRow][newCol];

        if(hitted != null && hitted.color == king.color)
            return false;

        board[king.row][king.col] = null;
        board[newRow][newCol] = king;

        int oldRow = king.row;
        int oldCol = king.col;
        king.row = newRow;
        king.col = newCol;

        boolean inSah = isKingInCheck(king);

        king.row = oldRow;
        king.col = oldCol;
        board[oldRow][oldCol] = king;
        board[newRow][newCol] = hitted;

        return !inSah;
    }

    private boolean canSaveking(Pieces king)
    {
        for(Pieces piece : piecesList) {
            if (piece.color != king.color)
                continue;

            if(piece.type == Type.KING)
                continue;

            for(int r = 0; r < 8; r++)
                for(int c = 0; c < 8; c++)
                {
                    if(!piece.move(r, c))
                        continue;

                    Pieces lovita = board[r][c];

                    board[piece.row][piece.col] = null;
                    board[r][c] = piece;

                    int oldRow = piece.row;
                    int oldCol = piece.col;
                    piece.row = r;
                    piece.col = c;

                    boolean inSah = isKingInCheck(king);

                    piece.row = oldRow;
                    piece.col = oldCol;
                    board[oldRow][oldCol] = piece;
                    board[r][c] = lovita;

                    if (!inSah)
                        return true;
                }
        }

        return false;
    }

    private void checkPromotion(Pieces piece, int row, int col)
    {
        promoted = false;
        if(row == 7 && piece.color == ColorType.BLACK && piece.type == Type.PAWN) {
            board[row][col] = new Queen(piece.color, row, col, this);
            promoted = true;
        }
        else if (row == 0 && piece.color == ColorType.WHITE) {
            board[row][col] = new Queen(piece.color, row, col, this);
            promoted = true;
        }
    }

    public void resetMoveNotations() {
        moveNotation.resetNotations();
    }

    public String getAllPGN() {
        return moveNotation.allFormattedMoves;
    }

    public String getCurrentFen() { return moveNotation.currentFEN;}
    public List<String> getAllFEN() { return moveNotation.getFENList(); }

    public int getMovesPlayed() {
        return moveNotation.movesPlayed;
    }

    public Sides getWinner() {
        return this.winner;
    }

    public void setWinner(Sides winner) {
        this.winner = winner;
    }

    public ResultType convertToResult() {
        return (winner == Sides.WHITE) ? ResultType.WHITE_WIN : ResultType.BLACK_WIN;
    }

    public boolean getResignation() {
        return resignation;
    }

    public void setResignation(boolean resignation) {
        this.resignation = resignation;
    }

    public LastMove getLastMove() {
        return lastMove;
    }
}
