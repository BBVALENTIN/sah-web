package com.sah.controller;

import com.sah.dto.PiesaDTO;
import com.sah.game.ChessBoard;
import com.sah.dto.MoveResult;
import com.sah.game.piese.Piese;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessController {
    private final ChessBoard chessBoard;

    @Autowired
    public ChessController(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    @PostMapping("/move")
    public MoveResult move(@RequestParam int fromRow,
                           @RequestParam int fromCol,
                           @RequestParam int toRow,
                           @RequestParam int toCol
    ) {
        return chessBoard.faMiscare(fromRow, fromCol, toRow, toCol);
    }

    @GetMapping("/state")
    public List<PiesaDTO> getState() {
        return chessBoard.getAllPiecesDTO();
    }

    @GetMapping("/turn")
    public int getCuloareCurenta()
    {
        return ChessBoard.getCuloareCurenta();
    }


    @GetMapping("/reset")
    public List<PiesaDTO> ResetBoard()
    {
        chessBoard.pieseList.clear();
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                chessBoard.board[i][j] = null;
        chessBoard.allFormatedMoves = "";
        chessBoard.initializeBoard();
        List<PiesaDTO> dto = new ArrayList<>();
        for(Piese p : chessBoard.getAllPieces())
            dto.add(ChessBoard.toDTO(p));
        return dto;
    }

    @GetMapping("/PGN")
    public String getPGN()
    {
        return chessBoard.allFormatedMoves;
    }
}

