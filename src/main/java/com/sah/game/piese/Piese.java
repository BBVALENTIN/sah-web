package com.sah.game.piese;

import com.sah.game.ChessBoard;
import com.sah.game.Tip;

public class Piese {
    public int x, y;
    public int col, row, precol, prerow;
    public int color;
    public boolean miscata;
    public Piese lovestePiese;
    public String img;
    public Tip TIP;

    public Piese(int color, int row, int col)
    {
        this.color = color;
        this.col = col;
        this.row = row;
        precol = col;
        prerow = row;
        this.miscata = false;
    }
    public void setPosition(int row, int col) {
        this.prerow = this.row;
        this.precol = this.col;
        this.row = row;
        this.col = col;
        this.miscata = true;
    }
    public Piese getLovesteP(int targetRow, int targetCol) {
        Piese lovestePiese = ChessBoard.board[targetRow][targetCol];
        if (lovestePiese != null && lovestePiese != this) {
            return lovestePiese;
        }
        return null;
    }


    public boolean peTabla(int targetCol, int targetRow)
    {
        return targetCol >= 0 && targetRow >=0 && targetCol <= 7 && targetRow <= 7;
    }

    public boolean piesaInFata(int targetRow, int targetCol) {
        if (this.row == targetRow) {
            int start = Math.min(this.col, targetCol) + 1;
            int end = Math.max(this.col, targetCol);
            for (int c = start; c < end; c++) {
                Piese piesa = ChessBoard.board[this.row][c];
                if (piesa != null) {
                    lovestePiese = piesa;
                    return true;
                }
            }
        }

        if (this.col == targetCol) {
            int start = Math.min(this.row, targetRow) + 1;
            int end = Math.max(this.row, targetRow);
            for (int r = start; r < end; r++) {
                Piese piesa = ChessBoard.board[r][this.col];
                if (piesa != null) {
                    lovestePiese = piesa;
                    return true;
                }
            }
        }

        return false;
    }


    public boolean patratValid(int targetRow, int targetCol) {
        lovestePiese = getLovesteP(targetRow, targetCol);
        if (lovestePiese == null) {
            return true;
        }
        if(lovestePiese != null && lovestePiese.color != this.color){
            return true;
        }
        return false;
    }

    public boolean acelasiPatrat(int targetRow, int targetCol) {
        return (targetRow == this.row && targetCol == this.col);
    }


    public boolean miscare(int targetRow, int targetCol)
    {
        return false;
    }

    public boolean piesaPeDiagonala(int targetRow, int targetCol) {
        // sus-stanga
        if (targetRow < this.row && targetCol < this.col) {
            int i = this.row - 1;
            int j = this.col - 1;
            while (i > targetRow && j > targetCol) {
                if (ChessBoard.board[i][j] != null) {
                    lovestePiese = ChessBoard.board[i][j];
                    return true;
                }
                i--;
                j--;
            }
        }

        // sus-dreapta
        if (targetRow < this.row && targetCol > this.col) {
            int i = this.row - 1;
            int j = this.col + 1;
            while (i > targetRow && j < targetCol) {
                if (ChessBoard.board[i][j] != null) {
                    lovestePiese = ChessBoard.board[i][j];
                    return true;
                }
                i--;
                j++;
            }
        }

        // jos-stanga
        if (targetRow > this.row && targetCol < this.col) {
            int i = this.row + 1;
            int j = this.col - 1;
            while (i < targetRow && j > targetCol) {
                if (ChessBoard.board[i][j] != null) {
                    lovestePiese = ChessBoard.board[i][j];
                    return true;
                }
                i++;
                j--;
            }
        }

        // mutare în jos-dreapta
        if (targetRow > this.row && targetCol > this.col) {
            int i = this.row + 1;
            int j = this.col + 1;
            while (i < targetRow && j < targetCol) {
                if (ChessBoard.board[i][j] != null) {
                    lovestePiese = ChessBoard.board[i][j];
                    return true;
                }
                i++;
                j++;
            }
        }

        return false;
    }


    public String getColorAsString() {
        if (this.color == 1) return "alb";
        else return "negru";
    }
}
