package com.sah.game;

import com.sah.game.dto.MoveResult;
import com.sah.game.piese.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChessBoard {
    public static Piese[][] board = new Piese[8][8];

    public static final int alb = 1;
    public static final int negru = -1;

    private int culoareCurenta = alb;

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
    }

    public MoveResult faMiscare(int fromRow, int fromCol, int targetRow, int targetCol) {
        Piese piesaSelectata = board[fromRow][fromCol];
        System.out.println("PIESA SELECTATA: "+piesaSelectata);
//        int nrPioni = 0;
//        for(int i = 0; i < 8; i++)
//            for(int j = 0; j < 8; j++)
//            {
//                if(board[i][j] !=null)
//                    System.out.println(board[i][j]+" "+i+" "+j);
//                Piese piesa = ChessBoard.board[i][j];
//                if(piesa instanceof Pion)
//                    nrPioni++;
//            }
        if (piesaSelectata == null) {
            return new MoveResult(false, "Nu există piesă pe poziția selectată", getAllPieces());
        }

        if (piesaSelectata.color != culoareCurenta) {
            return new MoveResult(false, "Nu este rândul acestei culori", getAllPieces());
        }

        if (!piesaSelectata.miscare(targetRow, targetCol)) {
            return new MoveResult(false, "Mutare ilegală", getAllPieces());
        }

        board[targetRow][targetCol] = piesaSelectata;
        board[fromRow][fromCol] = null;
        piesaSelectata.setPosition(targetRow, targetCol);
//        System.out.println("nr pioni"+ nrPioni);

        culoareCurenta *= -1;
        return new MoveResult(true, "Mutare validă", getAllPieces());
    }

    public List<Piese> getAllPieces() {
        List<Piese> pieseList = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] != null) {
                    pieseList.add(board[row][col]);
                }
            }
        }
        return pieseList;
    }

    public Piese getPiesa(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return board[row][col];
        }
        return null;
    }
}
