package com.sah.game;

import com.sah.dto.MoveDataNotationDTO;
import com.sah.dto.MoveResultDTO;
import com.sah.dto.PiesaDTO;
import com.sah.dto.LastMove;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.ErrorCodes;
import com.sah.game.GameEnums.NotatieRocada;
import com.sah.game.GameEnums.Tip;
import com.sah.game.piese.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
// daca e o mutare duplicata nu se mai insereaza anotatia
@Service
public class ChessBoard {
    public Piese[][] board = new Piese[8][8];
    public Piese rocada;
    public Piese sahP, piesaSelectata, piesaCapturata;
    public List<Piese> pieseList = new ArrayList<>();
    public short movesPlayed, halfMove;
    public boolean promoted, isCheckMate, resignation;
    private LastMove lastMove;
    private MoveNotation moveNotation = new MoveNotation();
    private Sides winner;
    private List<Piese> pieseCapturate = new ArrayList<>();

    public ColorType culoareCurenta = ColorType.ALB;


    public ChessBoard() {
        initializeBoard();
    }

    public void initializeBoard() {
        this.board = new Piese[8][8];

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pion(ColorType.NEGRU, 1, i, this);
        }
        board[0][0] = new Tura(ColorType.NEGRU, 0, 0, this);
        board[0][7] = new Tura(ColorType.NEGRU, 0, 7, this);
        board[0][1] = new Cal(ColorType.NEGRU, 0, 1, this);
        board[0][6] = new Cal(ColorType.NEGRU, 0, 6, this);
        board[0][2] = new Nebun(ColorType.NEGRU, 0, 2, this);
        board[0][5] = new Nebun(ColorType.NEGRU, 0, 5, this);
        board[0][3] = new Regina(ColorType.NEGRU, 0, 3, this);
        board[0][4] = new Rege(ColorType.NEGRU, 0, 4, this);

        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pion(ColorType.ALB, 6, i, this);
        }
        board[7][7] = new Tura(ColorType.ALB, 7, 7, this);
        board[7][0] = new Tura(ColorType.ALB, 7, 0, this);
        board[7][1] = new Cal(ColorType.ALB, 7, 1, this);
        board[7][6] = new Cal(ColorType.ALB, 7, 6, this);
        board[7][5] = new Nebun(ColorType.ALB, 7, 5, this);
        board[7][2] = new Nebun(ColorType.ALB, 7, 2, this);
        board[7][3] = new Regina(ColorType.ALB, 7, 3, this);
        board[7][4] = new Rege(ColorType.ALB, 7, 4, this);

        this.pieseList = getAllPieces();
        this.culoareCurenta = ColorType.ALB;
        this.isCheckMate = false;
        this.promoted = false;
        this.winner = null;
        this.movesPlayed = 1;
        this.moveNotation = new MoveNotation();
    }

    public synchronized MoveResultDTO faMiscare(int fromRow, int fromCol, int targetRow, int targetCol) {
        piesaSelectata = board[fromRow][fromCol];
        if (piesaSelectata == null) { // piesa inexistenta
            return new MoveResultDTO(ErrorCodes.PIESA_NEDETECTATA);
        }

        if (piesaSelectata.color != culoareCurenta) { // NU MUTA PIESA DE CULOAREA ASTA!
            return new MoveResultDTO(ErrorCodes.RAND_GRESIT);
        }
        boolean enPassant = false;
        if(piesaSelectata.tip == Tip.PAWN && this.lastMove != null) {

            if(isEnPassant(piesaSelectata, fromRow, fromCol, targetRow, targetCol) && canEnPassant(this.lastMove, piesaSelectata, targetRow, targetCol)) {
                applyEnPassant(piesaSelectata, targetRow, targetCol);
                enPassant = true;
            }
        }
        if (!piesaSelectata.miscare(targetRow, targetCol) && enPassant == false) { // mutare ilegala
            return new MoveResultDTO(ErrorCodes.MUTARE_ILEGALA);
        }
        NotatieRocada rocadaNotatie = null;

        if (rocada != null) {
            Piese rocadaCopy = rocada;
            makeRocada(rocada);
            rocadaNotatie = getRocadaType(rocadaCopy);
        }

        piesaCapturata = getPiesaCapturata(targetRow, targetCol);
        boolean isCapture = false;
        if(piesaCapturata != null) {
            isCapture = true;
            pieseCapturate.add(piesaCapturata);
        }

        List<Piese> oldList = new ArrayList<>(pieseList);


        mutarePiesaSelectata(fromRow, fromCol, targetRow, targetCol, piesaSelectata); // de testat cazul in car esteRegeleMeuInSah true

        if(piesaSelectata.tip == Tip.PAWN)
        {
            checkPromotion(piesaSelectata, targetRow, targetCol);
        }
        getAllPieces();

        if(esteRegeleMeuInSah())
        {
            rollBack(targetRow, targetCol, fromRow, fromCol, piesaSelectata);
            return new MoveResultDTO(ErrorCodes.MUTARE_ILEGALA);
        }
        lastMove = new LastMove(fromRow, fromCol, targetRow, targetCol, toDTO(piesaSelectata, targetRow, targetCol));
        switchTurn();

        Piese regeAdvers = getRege(false);

        boolean isCheck = esteRegeInSah(regeAdvers);

        if (isCheck) {
            isCheckMate = esteSahMat(regeAdvers);
        }
        increaseHalfMove(piesaSelectata, isCapture);
        MoveDataNotationDTO dto = new MoveDataNotationDTO(piesaSelectata, fromRow, fromCol, targetRow, targetCol, isCheck, isCheckMate, promoted, isCapture, culoareCurenta, rocadaNotatie, oldList);
        String currentFormattedMove = moveNotation.formatMove(dto);
        String currentFEN = moveNotation.generateFEN(board, culoareCurenta, halfMove);

        promoted = false;
        if(isCheckMate) {
            winner = (culoareCurenta == ColorType.ALB) ? Sides.BLACK : Sides.WHITE;
            return new MoveResultDTO(getAllPiecesDTO(), isCheck, isCheckMate, ColorType.OVER, currentFormattedMove, isCapture, lastMove, currentFEN, convertToPiecesDTO(pieseCapturate));
        }

        return new MoveResultDTO(
                getAllPiecesDTO(),
                isCheck,
                isCheckMate,
                culoareCurenta,
                currentFormattedMove,
                isCapture,
                lastMove,
                currentFEN,
                convertToPiecesDTO(pieseCapturate)
        );
    }

    public synchronized List<Piese> getAllPieces() {
        pieseList.clear();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null) {
                    pieseList.add(board[row][col]);
                }
            }
        }
        return pieseList;
    }

    private boolean isEnPassant(Piese pawn, int fromRow, int fromCol, int targetRow, int targetCol) {
        if(pawn.tip != Tip.PAWN) return false;

        int dir = (pawn.color == ColorType.ALB) ? -1 : 1;

        return Math.abs(fromCol-targetCol) == 1 && targetRow == fromRow + dir;
    }

    private boolean canEnPassant(LastMove lastMove, Piese pawn, int targetRow, int targetCol) {
        if(lastMove == null || lastMove.getLastPiece().getTip() != Tip.PAWN) return false;

        boolean movedTwoSquares = Math.abs(lastMove.getFromRow() - lastMove.getToRow()) == 2;

        return movedTwoSquares && lastMove.getToRow() == pawn.row && lastMove.getToCol() == targetCol;
    }

    private void applyEnPassant(Piese pawn, int targetRow, int targetCol) {
        int dir = (pawn.color == ColorType.ALB) ? 1 : -1;

        Piese capturedPawn = board[targetRow + dir][targetCol];

        if(capturedPawn != null && capturedPawn.tip == Tip.PAWN) {
            board[targetRow+dir][targetCol] = null;
            pieseCapturate.add(capturedPawn);
        }
    }

    private void makeRocada(Piese rocada) {
        int rookOldRow = rocada.row;
        int rookOldCol = rocada.col;
        int rookNewCol = (rocada.col == 7) ? 5 : 3;

        board[rookOldRow][rookNewCol] = rocada;
        board[rookOldRow][rookOldCol] = null;
        rocada.setPosition(rookOldRow, rookNewCol);

        this.rocada = null;
    }

    private NotatieRocada getRocadaType(Piese rocada) {
        if(rocada.col == 7)
            return NotatieRocada.MARE;
        else
            return NotatieRocada.MICA;
    }

    private void increaseHalfMove(Piese piesaSelectata, boolean isCapture) {
        if(piesaSelectata.tip == Tip.PAWN || isCapture) {
            halfMove = 0;
        }
        else {
            halfMove++;
        }
    }

    private void mutarePiesaSelectata(int fromRow, int fromCol, int targetRow, int targetCol, Piese piesaSelectata)
    {
        board[fromRow][fromCol] = null;
        board[targetRow][targetCol] = piesaSelectata;

        piesaSelectata.setPosition(targetRow, targetCol);
    }

    private void rollBack(int targetRow, int targetCol, int fromRow, int fromCol, Piese piesaSelectata) {
        board[fromRow][fromCol] = piesaSelectata;
        if(piesaCapturata == null) {
            board[targetRow][targetCol] = null;
        }
        else {
            board[targetRow][targetCol] = piesaCapturata;
        }
        piesaSelectata.setPosition(fromRow, fromCol);
    }


    public Piese getPiesaCapturata(int targetRow, int targetCol) {
        return board[targetRow][targetCol];
    }

    private void switchTurn() {
        if(culoareCurenta == ColorType.ALB)
            culoareCurenta = ColorType.NEGRU;
        else
            culoareCurenta = ColorType.ALB;
        movesPlayed++;
    }

    public ColorType getCuloareCurenta() {
        return culoareCurenta;
    }


    public Piese getRege(boolean opponent) {
        for (Piese piesa : pieseList) {
            if (piesa.tip == Tip.KING) {
                if (opponent && piesa.color != culoareCurenta)
                    return piesa;
                if (!opponent && piesa.color == culoareCurenta)
                    return piesa;
            }
        }
        return null;
    }


    private boolean esteRegeleMeuInSah() {
        Piese rege = getRege(false);
        return rege != null && esteRegeInSah(rege);
    }

    public boolean esteRegeInSah(Piese rege) {
        for (Piese piesa : pieseList) {
            if (piesa.color != rege.color) {
                if (piesa.miscare(rege.row, rege.col)) {
                    sahP = piesa;
                    return true;
                }
            }
        }
        sahP = null;
        return false;
    }

    public static PiesaDTO toDTO(Piese p, int r, int c) {
        return new PiesaDTO(
                p.tip,
                p.color,
                r,
                c
        );
    }

    public Piese[][] getBoard() {
        return board;
    }

    public List<PiesaDTO> getAllPiecesDTO() {
        List<PiesaDTO> dto = new ArrayList<>();
        for(int r = 0; r < 8; r++) {
            for(int c = 0; c < 8; c++) {
                Piese p = board[r][c];
                if(p != null) {
                    dto.add(toDTO(p, r, c));
                }
            }
        }
        return dto;
    }

    private List<PiesaDTO> convertToPiecesDTO(List<Piese> list) {
        List<PiesaDTO> dto = new ArrayList<>();
        if(list == null) {
            return dto;
        }
        for(Piese p : list) {
            dto.add(toDTO(p, p.row, p.col));
        }
        return dto;
    }

    private boolean esteSahMat(Piese rege)
    {
        if(!esteRegeInSah(rege))
            return false;
        if(miscareRege(rege))
            return false;
        if(canSaveRege(rege))
            return false;
        return true;
    }

    private boolean miscareRege(Piese rege){
        if(isValidMoveRege(rege, -1, -1)) return true;
        if(isValidMoveRege(rege, -1, -0)) return true;
        if(isValidMoveRege(rege, -1, 1)) return true;
        if(isValidMoveRege(rege, 0, -1)) return true;
        if(isValidMoveRege(rege, 0, 1)) return true;
        if(isValidMoveRege(rege, 1, -1)) return true;
        if(isValidMoveRege(rege, 1, 0)) return true;
        if(isValidMoveRege(rege, 1, 1)) return true;

        return false;
    }
    private boolean isValidMoveRege(Piese rege, int rowSafe, int colSafe)
    {
        int newRow = rege.row + rowSafe;
        int newCol = rege.col + colSafe;

        if(!rege.peTabla(newRow, newCol)) return false;

        Piese lovita = board[newRow][newCol];

        if(lovita != null && lovita.color == rege.color)
            return false;

        board[rege.row][rege.col] = null;
        board[newRow][newCol] = rege;

        int oldRow = rege.row;
        int oldCol = rege.col;
        rege.row = newRow;
        rege.col = newCol;

        boolean inSah = esteRegeInSah(rege);

        // rollback
        rege.row = oldRow;
        rege.col = oldCol;
        board[oldRow][oldCol] = rege;
        board[newRow][newCol] = lovita;

        return !inSah;
    }

    private boolean canSaveRege(Piese rege)
    {
        for(Piese piesa : pieseList) {
            if (piesa.color != rege.color)
                continue; // salvam piesele de aceeasi culoare cu regele nostru

            if(piesa.tip == Tip.KING)
                continue;

            for(int r = 0; r < 8; r++)
                for(int c = 0; c < 8; c++)
                {
                    if(!piesa.miscare(r, c))
                        continue;

                    Piese lovita = board[r][c];

                    board[piesa.row][piesa.col] = null;
                    board[r][c] = piesa;

                    int oldRow = piesa.row;
                    int oldCol = piesa.col;
                    piesa.row = r;
                    piesa.col = c;

                    boolean inSah = esteRegeInSah(rege);

                    piesa.row = oldRow;
                    piesa.col = oldCol;
                    board[oldRow][oldCol] = piesa;
                    board[r][c] = lovita;

                    if (!inSah)
                        return true;
                }
        }

        return false;
    }

    private void checkPromotion(Piese piesa, int row, int col)
    {
        promoted = false;
        if(row == 7 && piesa.color == ColorType.NEGRU && piesa.tip == Tip.PAWN) {
            board[row][col] = new Regina(piesa.color, row, col, this);
            promoted = true;
        }
        else if (row == 0 && piesa.color == ColorType.ALB) {
            board[row][col] = new Regina(piesa.color, row, col, this);
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
        return (winner == Sides.WHITE) ? ResultType.WHITE_WIN : ResultType.BLACK_WIN; // for now
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
