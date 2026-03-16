package com.sah.game;

import com.sah.dto.MoveDataNotationDTO;
import com.sah.dto.MoveResultDTO;
import com.sah.dto.PiesaDTO;
import com.sah.dto.LastMove;
import com.sah.game.GameEnums.ErrorCodes;
import com.sah.game.GameEnums.NotatieRocada;
import com.sah.game.GameEnums.Tip;
import com.sah.game.piese.*;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
// daca e o mutare duplicata nu se mai insereaza anotatia
@Service
public class ChessBoard {
    public Piese[][] board = new Piese[8][8];
    public Piese rocada;
    public Piese rege, sahP, piesaSelectata, piesaCapturata;
    public List<Piese> pieseList = new ArrayList<>();
    public List<Piese> oldList = new ArrayList<>();
    public static final int alb = 1, negru = -1;
    public short numberOfMoves;
    public static short oldSize;
    public boolean promoted, isCheckMate;
    public ErrorCodes error;
    public LastMove lastMove;
    private MoveNotation moveNotation = new MoveNotation();

    public static int culoareCurenta = alb;


    public ChessBoard() {
        initializeBoard();
    }

    public void initializeBoard() {
        this.board = new Piese[8][8];

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pion(negru, 1, i, this);
        }
        board[0][0] = new Tura(negru, 0, 0, this);
        board[0][7] = new Tura(negru, 0, 7, this);
        board[0][1] = new Cal(negru, 0, 1, this);
        board[0][6] = new Cal(negru, 0, 6, this);
        board[0][2] = new Nebun(negru, 0, 2, this);
        board[0][5] = new Nebun(negru, 0, 5, this);
        board[0][3] = new Regina(negru, 0, 3, this);
        board[0][4] = new Rege(negru, 0, 4, this);

        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pion(alb, 6, i, this);
        }
        board[7][7] = new Tura(alb, 7, 7, this);
        board[7][0] = new Tura(alb, 7, 0, this);
        board[7][1] = new Cal(alb, 7, 1, this);
        board[7][6] = new Cal(alb, 7, 6, this);
        board[7][5] = new Nebun(alb, 7, 5, this);
        board[7][2] = new Nebun(alb, 7, 2, this);
        board[7][3] = new Regina(alb, 7, 3, this);
        board[7][4] = new Rege(alb, 7, 4, this);

        pieseList = getAllPieces();
        culoareCurenta = alb;
        numberOfMoves = 1;
    }

    public synchronized MoveResultDTO faMiscare(int fromRow, int fromCol, int targetRow, int targetCol) {
        piesaSelectata = board[fromRow][fromCol];
        lastMove = new LastMove(fromRow, fromCol, targetRow, targetCol);
        if (piesaSelectata == null) { // piesa inexistenta
            return new MoveResultDTO(error.PIESA_NEDETECTATA);
        }

        if (piesaSelectata.color != culoareCurenta) { // NU MUTA PIESA DE CULOAREA ASTA!
            return new MoveResultDTO(error.RAND_GRESIT);
        }
        if (!piesaSelectata.miscare(targetRow, targetCol)) { // mutare ilegala
            return new MoveResultDTO(error.MUTARE_ILEGALA);
        }

        NotatieRocada rocadaNotatie = null;

        if (rocada != null) {
            makeRocada(rocada);
            rocadaNotatie = getRocadaType(rocada);
        }

        piesaCapturata = getPiesaCapturata(targetRow, targetCol);
        boolean isCapture = piesaCapturata != null;

        oldList = new ArrayList<>(pieseList);


        mutarePiesaSelectata(fromRow, fromCol, targetRow, targetCol, piesaSelectata);

        if(piesaSelectata.tip == Tip.PION)
        {
            checkPromotion(piesaSelectata, targetRow, targetCol);
        }
        getAllPieces();

        if(esteRegeleMeuInSah())
        {
            rollBack(targetRow, targetCol, fromRow, fromCol, piesaSelectata);
            return new MoveResultDTO(ErrorCodes.MUTARE_ILEGALA);
        }

        switchTurn();

        Piese regeAdvers = getRege(false);

        boolean isCheck = esteRegeInSah(regeAdvers);

        if (isCheck) {
            isCheckMate = esteSahMat(regeAdvers);
        }

        MoveDataNotationDTO dto = new MoveDataNotationDTO(piesaSelectata, fromRow, fromCol, targetRow, targetCol, isCheck, isCheckMate, promoted, isCapture, culoareCurenta, rocadaNotatie, oldList);
        String currentFormattedMove = moveNotation.formatMove(dto);

        promoted = false;
        if(isCheckMate == true)
            return new MoveResultDTO(getAllPiecesDTO(), isCheck, isCheckMate, 0, currentFormattedMove, isCapture, lastMove);
        // sah, sah-mat
        return new MoveResultDTO(
                getAllPiecesDTO(),
                isCheck,
                isCheckMate,
                culoareCurenta,
                currentFormattedMove,
                isCapture,
                lastMove
        );
    }

    public synchronized List<Piese> getAllPieces() {
        oldSize = (short) pieseList.size();
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

    private void makeRocada(Piese rocada) {
        int rookOldRow = rocada.row;
        int rookOldCol = rocada.col;
        int rookNewCol = (rocada.col == 7) ? rookOldCol - 2 : rookOldCol + 3;

        board[rookOldRow][rookNewCol] = rocada;
        board[rookOldRow][rookOldCol] = null;
        rocada.setPosition(rookOldRow, rookNewCol);

        rocada = null;
    }

    private NotatieRocada getRocadaType(Piese rocada) {
        if(rocada.col == 7)
            return NotatieRocada.MARE;
        else
            return NotatieRocada.MICA;
    }

    public List<Piese> mutarePiesaSelectata(int fromRow, int fromCol, int targetRow, int targetCol, Piese piesaSelectata)
    {
        board[fromRow][fromCol] = null;
        board[targetRow][targetCol] = piesaSelectata;

        piesaSelectata.setPosition(targetRow, targetCol);

        return pieseList;
    }

    public Piese getPiesa(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        return null;
    }
    public void rollBack(int targetRow, int targetCol, int fromRow, int fromCol, Piese piesaSelectata) {
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

    public void switchTurn() {
        culoareCurenta *= -1;
        numberOfMoves++;
    }

    public static int getCuloareCurenta() {
        return culoareCurenta;
    }

    public void checkRocada()
    {
        if(rocada != null) {
            rocada.setPosition(rocada.row, rocada.col - 2);
        }
        rocada = null;
    }

    public Piese getRege(boolean opponent) {
        for (Piese piesa : pieseList) {
            if (piesa.tip == Tip.REGE) {
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

    private boolean esteRegeleAdversInSah() {
        Piese rege = getRege(true);
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
                p.tip.name(),
                p.color,
                r,
                c
        );
    }

    public Piese[][] getBoard() {
        return board;
    }

    public synchronized List<PiesaDTO> getAllPiecesDTO() {
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

    //debug rege
    public PiesaDTO getRegeDTO(boolean opponent) {
        Piese rege = getRege(opponent);
        if(rege == null)
            return null;
        return toDTO(rege, rege.row, rege.col); // maybe buggy
    }

    public boolean esteSahMat(Piese rege)
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

    public boolean canSaveRege(Piese rege)
    {
        for(Piese piesa : pieseList) {
            if (piesa.color != rege.color)
                continue; // salvam piesele de aceeasi culoare cu regele nostru

            if(piesa.tip == Tip.REGE)
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

    public void checkPromotion(Piese piesa, int row, int col)
    {
        promoted = false;
        if(row == 7 && piesa.color == negru && piesa.tip == Tip.PION) {
            board[row][col] = new Regina(piesa.color, row, col, this);
            promoted = true;
        }
        else if (row == 0 && piesa.color == alb) {
            board[row][col] = new Regina(piesa.color, row, col, this);
            promoted = true;
        }
    }
}
