package com.sah.game.pieces;

import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.GameEnums.Type;

public class Pieces {
    public int x, y;
    public int col, row, precol, prerow;
    public ColorType color;
    public boolean moved;
    public Pieces hittedPiece;
    public String img;
    public Type type;
    protected ChessBoard game;

    public Pieces(ColorType color, int row, int col, ChessBoard game)
    {
        this.color = color;
        this.col = col;
        this.row = row;
        precol = col;
        prerow = row;
        this.moved = false;
        this.game = game;
    }
    public void setPosition(int row, int col) {
        this.prerow = this.row;
        this.precol = this.col;
        this.row = row;
        this.col = col;
        this.moved = true;
    }

    public Pieces getHittedPiece(int targetRow, int targetCol) {
        Pieces hittedPiece = game.board[targetRow][targetCol];
        if (hittedPiece != null && hittedPiece != this) {
            return hittedPiece;
        }
        return null;
    }


    public boolean onTable(int targetRow, int targetCol)
    {
        return targetCol >= 0 && targetRow >=0 && targetCol <= 7 && targetRow <= 7;
    }

    public boolean canGetTo(int targetRow, int targetCol){
        return false;
    }

    public boolean pieceInFront(int targetRow, int targetCol) {
        if (this.row == targetRow) {
            int start = Math.min(this.col, targetCol) + 1;
            int end = Math.max(this.col, targetCol);
            for (int c = start; c < end; c++) {
                Pieces piesa = game.board[this.row][c];
                if (piesa != null) {
                    hittedPiece = piesa;
                    return true;
                }
            }
        }

        if (this.col == targetCol) {
            int start = Math.min(this.row, targetRow) + 1;
            int end = Math.max(this.row, targetRow);
            for (int r = start; r < end; r++) {
                Pieces piesa = game.board[r][this.col];
                if (piesa != null) {
                    hittedPiece = piesa;
                    return true;
                }
            }
        }

        return false;
    }


    public boolean validSquare(int targetRow, int targetCol) {
        hittedPiece = getHittedPiece(targetRow, targetCol);
        if (hittedPiece == null) {
            return true;
        }
        if(hittedPiece != null && hittedPiece.color != this.color){
            return true;
        }
        return false;
    }

    public boolean sameSquare(int targetRow, int targetCol) {
        return (targetRow == this.row && targetCol == this.col);
    }


    public boolean move(int targetRow, int targetCol)
    {
        return false;
    }

    public boolean pieceOnDiagonal(int targetRow, int targetCol) {
        // top-left
        if (targetRow < this.row && targetCol < this.col) {
            int i = this.row - 1;
            int j = this.col - 1;
            while (i > targetRow && j > targetCol) {
                if (game.board[i][j] != null) {
                    hittedPiece = game.board[i][j];
                    return true;
                }
                i--;
                j--;
            }
        }

        // top-right
        if (targetRow < this.row && targetCol > this.col) {
            int i = this.row - 1;
            int j = this.col + 1;
            while (i > targetRow && j < targetCol) {
                if (game.board[i][j] != null) {
                    hittedPiece = game.board[i][j];
                    return true;
                }
                i--;
                j++;
            }
        }

        // bottom-left
        if (targetRow > this.row && targetCol < this.col) {
            int i = this.row + 1;
            int j = this.col - 1;
            while (i < targetRow && j > targetCol) {
                if (game.board[i][j] != null) {
                    hittedPiece = game.board[i][j];
                    return true;
                }
                i++;
                j--;
            }
        }

        // bottom right
        if (targetRow > this.row && targetCol > this.col) {
            int i = this.row + 1;
            int j = this.col + 1;
            while (i < targetRow && j < targetCol) {
                if (game.board[i][j] != null) {
                    hittedPiece = game.board[i][j];
                    return true;
                }
                i++;
                j++;
            }
        }

        return false;
    }
}
