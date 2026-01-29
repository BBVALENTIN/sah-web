package com.sah.game;

import com.sah.dto.MoveResult;
import com.sah.dto.PiesaDTO;
import com.sah.game.piese.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChessBoard {
    public static Piese[][] board = new Piese[8][8];
    public static Piese rocada;
    public  Piese rege, sahP, piesaSelectata, piesaCapturata;
    public List<Piese> pieseList = new ArrayList<>();
    public List<Piese> oldList = new ArrayList<>();
    public static final int alb = 1;
    public static final int negru = -1;
    public String allFormattedMoves = "", currentFormattedMove;
    public short numberOfMoves;
    public static short oldSize;
    public boolean promoted, isCheckMate;
    public ErrorCodes error;

    private static int culoareCurenta = alb;


    public ChessBoard() {
        initializeBoard();
    }

    public void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pion(negru, 1, i);
        }
        board[0][0] = new Tura(negru, 0, 0);
        board[0][7] = new Tura(negru, 0, 7);
        board[0][1] = new Cal(negru, 0, 1);
        board[0][6] = new Cal(negru, 0, 6);
        board[0][2] = new Nebun(negru, 0, 2);
        board[0][5] = new Nebun(negru, 0, 5);
        board[0][3] = new Regina(negru, 0, 3);
        board[0][4] = new Rege(negru, 0, 4);

        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pion(alb, 6, i);
        }
        board[7][7] = new Tura(alb, 7, 7);
        board[7][0] = new Tura(alb, 7, 0);
        board[7][1] = new Cal(alb, 7, 1);
        board[7][6] = new Cal(alb, 7, 6);
        board[7][5] = new Nebun(alb, 7, 5);
        board[7][2] = new Nebun(alb, 7, 2);
        board[7][3] = new Regina(alb, 7, 3);
        board[7][4] = new Rege(alb, 7, 4);

        pieseList = getAllPieces();
        culoareCurenta = alb;
        numberOfMoves = 1;
    }

    public synchronized MoveResult faMiscare(int fromRow, int fromCol, int targetRow, int targetCol) {

        piesaSelectata = board[fromRow][fromCol];
        if (piesaSelectata == null) { // piesa inexistenta
            return new MoveResult(error.PIESA_NEDETECTATA);
        }

        if (piesaSelectata.color != culoareCurenta) { // rand gresit
            return new MoveResult(error.RAND_GRESIT);
        }
        if (!piesaSelectata.miscare(targetRow, targetCol)) { // mutare ilegala
            return new MoveResult(error.MUTARE_ILEGALA);
        }

        String rocadaNotatie = null;

        if (rocada != null) {
            if (rocada.col == 7) {
                rocadaNotatie = "Rocada-Mica"; // O-O
            } else {
                rocadaNotatie = "Rocada-Mare"; // O-O-O
            }

            int rookOldRow = rocada.row;
            int rookOldCol = rocada.col;
            int rookNewCol = (rocada.col == 7) ? rookOldCol - 2 : rookOldCol + 3;

            board[rookOldRow][rookNewCol] = rocada;
            board[rookOldRow][rookOldCol] = null;
            rocada.setPosition(rookOldRow, rookNewCol);

            rocada = null;
        }
        boolean isCapture = board[targetRow][targetCol] != null;
        if(isCapture == true) {
            piesaCapturata = board[targetRow][targetCol];
        }
        else piesaCapturata = null;
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
            return new MoveResult(getAllPiecesDTO(), true, false, culoareCurenta, currentFormattedMove, isCapture);
        }


        culoareCurenta *= -1;
        numberOfMoves++;

        Piese regeAdvers = getRege(false);

        boolean isCheck = esteRegeInSah(regeAdvers);
        isCheckMate = esteSahMat(regeAdvers);

        if (isCheck) {
            isCheckMate = esteSahMat(regeAdvers);

        }


        allFormatedMoves(formattedMoves(piesaSelectata, fromRow, fromCol, targetRow, targetCol, isCheck, isCheckMate, rocadaNotatie, isCapture));
        promoted = false;
        if(isCheckMate == true)
            return new MoveResult(getAllPiecesDTO(), isCheck, isCheckMate, 0, currentFormattedMove, isCapture);
        // sah, sah-mat
        return new MoveResult(
                getAllPiecesDTO(),
                isCheck,
                isCheckMate,
                culoareCurenta,
                currentFormattedMove,
                isCapture
        );
//        return new MoveResult(true, "Mutare validă", getAllPiecesDTO(), isCheck, isCheckMate);
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

    public static PiesaDTO toDTO(Piese p) {
        return new PiesaDTO(
                p.tip.name(),
                p.color,
                p.row,
                p.col
        );
    }

    public synchronized List<PiesaDTO> getAllPiecesDTO() {
        List<PiesaDTO> dto = new ArrayList<>();
        for (Piese p : pieseList) {
            dto.add(toDTO(p));
        }
        return dto;
    }

    //debug rege
    public PiesaDTO getRegeDTO(boolean opponent) {
        Piese rege = getRege(opponent);
        if(rege == null)
            return null;
        return toDTO(rege);
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
            board[row][col] = new Regina(piesa.color, row, col);
            promoted = true;
        }
        else if (row == 0 && piesa.color == alb) {
            board[row][col] = new Regina(piesa.color, row, col);
            promoted = true;
        }
    }
    // moving a pawn to row 4 (from up to bottom), col 4 = e4
    public String formattedMoves(Piese piesa,int fromRow, int fromCol, int targetRow, int targetCol, boolean isCheck, boolean isCheckMate, String rocadaNotatie, boolean isCapture)
    {
        if(rocadaNotatie != null)
        {
            if ("Rocada-Mare".equals(rocadaNotatie)) { return"O-O-O";}
            if ("Rocada-Mica".equals(rocadaNotatie)) { return "O-O";}
        }

        char pieceChar;
        switch(piesa.tip){
            case CAL -> pieceChar = 'N';
            case NEBUN -> pieceChar = 'B';
            case REGE ->  pieceChar = 'K';
            case REGINA -> pieceChar = 'Q';
            case TURA ->  pieceChar = 'R';
            default -> pieceChar = '?';
        }

        char colChar = (char)('a'+targetCol);
        int boardRow = 8 - targetRow;

        char fromColChar = (char)('a'+fromCol);
        int fromBoardRow = 8 - fromRow;

        String disambiguation = "";
        if(piesa.tip != Tip.PION)
        {
            for(Piese p: oldList)
            {
                if(p == piesa)
                    continue;
                if((p.color == piesa.color && piesa.tip == p.tip) && p.poateAjunge(targetRow, targetCol))
                {
                    if(p.col == fromCol) {
                        disambiguation = "" + fromBoardRow;
                    }
                    else {
                        disambiguation = "" + fromColChar;
                    }
                }
            }
        }

        String notation = "";

        if(piesa.tip == Tip.PION) {
            if (isCapture)
            {
                notation = fromColChar+"x"+colChar+boardRow;
            }
            else {
                notation = ""+colChar+boardRow;
            }
        }
        else
        {
            if(isCapture)
                notation = "" + pieceChar + disambiguation + "x" + colChar + boardRow;
            else
                notation = "" + pieceChar + disambiguation + colChar + boardRow;
        }

        if(promoted == true){
            notation = notation + "=Q";
        }
        if(isCheck && !isCheckMate) {
            notation = notation + "+";
        }
        if(isCheckMate)
        {
            if(this.culoareCurenta == 1)
                notation = notation + "#" + " 0-1";
            else
                notation = notation + "#" + " 1-0";
        }

        return notation;
    }

    public void allFormatedMoves(String notation)
    {
        if(numberOfMoves%2 ==0)
            allFormattedMoves += numberOfMoves/2 + ".";
        currentFormattedMove = notation;
        allFormattedMoves += notation + " ";
    }

}
