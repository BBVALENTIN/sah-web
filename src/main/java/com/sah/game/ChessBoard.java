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
    public  Piese rege, sahP, piesaSelectata;
    public List<Piese> pieseList = new ArrayList<>();
    public static final int alb = 1;
    public static final int negru = -1;

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
//        board[7][1] = new Cal(alb, 7, 1);
//        board[7][6] = new Cal(alb, 7, 6);
//        board[7][5] = new Nebun(alb, 7, 5);
//        board[7][2] = new Nebun(alb, 7, 2);
        board[7][3] = new Regina(alb, 7, 3);
        board[7][4] = new Rege(alb, 7, 4);
        pieseList = getAllPieces();
        culoareCurenta = alb;
    }

    public MoveResult faMiscare(int fromRow, int fromCol, int targetRow, int targetCol) {
        piesaSelectata = board[fromRow][fromCol];
        System.out.println("PIESA SELECTATA: "+piesaSelectata);
        int nrPioni = 0;
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(board[i][j] !=null)
                    System.out.println(board[i][j]+" "+i+" "+j);
                Piese piesa = ChessBoard.board[i][j];
                if(piesa instanceof Pion)
                    nrPioni++;
            }
        if (piesaSelectata == null) {
            return new MoveResult(false, "Nu există piesă pe poziția selectată", getAllPiecesDTO());
        }

        if (piesaSelectata.color != culoareCurenta) {
            return new MoveResult(false, "Nu este rândul acestei culori", getAllPiecesDTO());
        }

        if (!piesaSelectata.miscare(targetRow, targetCol)) {
            return new MoveResult(false, "Mutare ilegală", getAllPiecesDTO());
        }

        if (rocada != null) {

            int rookOldRow = rocada.row;
            int rookOldCol = rocada.col;
            int rookNewCol = rookOldCol - 2;

            board[rookOldRow][rookNewCol] = rocada;

            board[rookOldRow][rookOldCol] = null;

            rocada.setPosition(rookOldRow, rookNewCol);

            rocada = null;
        }

        mutarePiesaSelectata(fromRow, fromCol, targetRow, targetCol, piesaSelectata);

        if(esteRegeleMeuInSah())
        {
            board[fromRow][fromCol] = piesaSelectata;
            board[targetRow][targetCol] = null;

            piesaSelectata.setPosition(fromRow, fromCol);

            return new MoveResult(false, "Nu-ti poti lasa regele in sah", getAllPiecesDTO());
        }

//        System.out.println("nr pioni"+ nrPioni);

        culoareCurenta *= -1;
        return new MoveResult(true, "Mutare validă", getAllPiecesDTO());
    }

    public List<Piese> getAllPieces() {
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


    private boolean esteRegeInSah(Piese rege) {
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

    public List<PiesaDTO> getAllPiecesDTO() {
        List<PiesaDTO> dto = new ArrayList<>();
        for (Piese p : getAllPieces()) {
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

        Piese lovita = ChessBoard.board[newRow][newCol];

        if(lovita != null || lovita.color == rege.color)
            return false;

        ChessBoard.board[rege.row][rege.col] = null;
        ChessBoard.board[newRow][newCol] = rege;

        int oldRow = rege.row;
        int oldCol = rege.col;
        rege.row = newRow;
        rege.col = newCol;

        boolean inSah = esteRegeInSah(rege);

        // rollback
        rege.row = oldRow;
        rege.col = oldCol;
        ChessBoard.board[oldRow][oldCol] = rege;
        ChessBoard.board[newRow][newCol] = lovita;

        return !inSah;
    }
}
