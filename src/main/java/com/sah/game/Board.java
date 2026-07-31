package com.sah.game;

import com.sah.config.AppConstants;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.gameenums.ColorType;
import com.sah.game.gameenums.Type;
import lombok.Getter;

@Getter
public class Board {
    private final Piece[][] squares = new Piece[8][8];

    public Board() {
        initializeFromFEN(AppConstants.startingFEN);
    }

    public Board(String fen)
    {
        initializeFromFEN(fen);
    }

    public Board(Board other)
    {
        for(int r = 0; r < 8; r++)
            for(int c =0; c < 8; c++)
            {
                Piece p = other.squares[r][c];
                if(p != null) {
                    squares[r][c] = p.copy();
                }
            }
    }

    private void initializeFromFEN(String FEN)
    {
        String[] parts = FEN.split(" ");
        String[] ranks = parts[0].split("/");
        for(int r = 0; r < 8; r++)
        {
            int col = 0;
            for(char ch : ranks[r].toCharArray())
            {
                if(Character.isDigit(ch))
                    continue;
                else
                {
                    squares[r][col] = mapToPiece(ch);
                    System.out.println("Piece: " + at(r, col).type);
                    col++;
                }
            }
        }
    }

    private Piece mapToPiece(char pieceChar)
    {
        ColorType color = Character.isUpperCase(pieceChar) ? ColorType.WHITE : ColorType.BLACK;
        return switch(Character.toLowerCase(pieceChar)) {
            case 'r' -> Piece.of(color, Type.ROOK);
            case 'n' -> Piece.of(color, Type.KNIGHT);
            case 'b' -> Piece.of(color, Type.BISHOP);
            case 'q' -> Piece.of(color, Type.QUEEN);
            case 'k' -> Piece.of(color, Type.KING);
            case 'p' -> Piece.of(color, Type.PAWN);
            default -> throw new IllegalArgumentException("Unknown: " + pieceChar);
        };
    }

    public Piece at(int row, int col)
    {
        return squares[row][col];
    }

    public boolean isEmpty(int row, int col)
    {
        return squares[row][col] == null;
    }

   public void movePiece(int fR, int fC, int tR, int tC)
    {
        Piece piece = squares[fR][fC];
        squares[tR][tC] = piece;
        squares[fR][fC] = null;
        piece.moved = true;
    }

    public boolean isPathClear(int fR, int fC, int tR, int tC)
    {
        int dR = Integer.compare(tR, fR);
        int dC = Integer.compare(tC, fC);
        int r = fR + dR;
        int c = fC + dC;

        while(r != tR || c != tC)
        {
            if(squares[r][c] != null) return false;

            r += dR;
            c += dC;
        }
        return true;
    }

    public boolean canMove(int fR, int fC, int tR, int tC)
    {
        Piece piece = squares[fR][fC];
        if(piece == null)
            return false;

        if(!MoveGeometry.isValid(piece.type, piece.color, fR, fC, tR, tC))
            return false;

        if(piece.type == Type.PAWN)
        {
            return isLegalPawnMove(piece.color, fR, fC, tR, tC);
        }

        if(piece.type != Type.KNIGHT && !isPathClear(fR, fC, tR, tC))
            return false;

        Piece target = squares[tR][tC];
        if(target != null && target.color == piece.color)
            return false;

        return true;
    }

    public boolean isLegalPawnMove(ColorType color, int fR, int fC, int tR, int tC) {
        int dRow = tR - fR;
        int dCol = Math.abs(tC - fC);

        int direction = (color == ColorType.WHITE) ? -1 : 1;
        int startRow = (color == ColorType.WHITE) ? 6 : 1;
        Piece target = squares[tR][tC];

        if(dCol == 0)
        {
            if(target != null) return false;
            if(dRow == direction) return true;

            if(dRow == 2 * direction && fR == startRow) {
                int midR = fR + direction;
                return squares[midR][fC] == null;
            }
        }

        if(dCol == 1 && dRow == direction)
        {
            if(target != null && target.color != color)
                return true;

            // check en passant in future

            return false;
        }
        return false;
    }

    public void executeKingCastle(ColorType color) {
        int kingRow = 0;
        if(color == ColorType.WHITE) {
             kingRow = 7;
        }

        Piece king = this.squares[kingRow][4];
        Piece smallCastleRook = this.squares[kingRow][7];
        movePiece(kingRow, 4, 7, 6);
        movePiece(kingRow, 7, 7, 5);
        // modify rights
    }

    public void executeQueenCastle(ColorType color) {
        int kingRow = 0;
        if(color == ColorType.WHITE) {
            kingRow = 7;
        }
    }

    public boolean canAttack(int row, int col, int attackedRow, int attackedCol) {

        Piece attacker = at(row, col);

        if(MoveGeometry.isValid(attacker.type, attacker.color, row, col, attackedRow, attackedCol) == false)
            return false;

        if(attacker.type == Type.PAWN) {
            int dCol = Math.abs(attackedCol - col);
            int dRow = attackedRow - row;
            int direction = (attacker.color == ColorType.WHITE) ? -1 : 1;
            return dCol == 1 && dRow == direction;
        }

        if(attacker.type != Type.KNIGHT && !isPathClear(row, col, attackedRow, attackedCol))
            return false;

        return true;
    }

    public void setSquare(int tr, int tc, Piece piece) {
        this.squares[tr][tc] = piece;
    }
}
