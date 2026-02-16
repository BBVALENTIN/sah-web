package com.sah.controller;

import com.sah.dto.PiesaDTO;
import com.sah.game.ChessBoard;
import com.sah.dto.MoveResultDTO;
import com.sah.game.piese.Piese;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessApiController {
    private final ChessBoard chessBoard;

    @Autowired
    public ChessApiController(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestParam int fromRow,
                           @RequestParam int fromCol,
                           @RequestParam int toRow,
                           @RequestParam int toCol
    ) {
        MoveResultDTO result =  chessBoard.faMiscare(fromRow, fromCol, toRow, toCol);

        if(result.getErrorCodes() != null) {
            return ResponseEntity.badRequest().body(result.getErrorCodes());
        }

        return ResponseEntity.ok(result);
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
        chessBoard.allFormattedMoves = "";
        chessBoard.initializeBoard();
        List<PiesaDTO> dto = new ArrayList<>();
        for(Piese p : chessBoard.getAllPieces())
            dto.add(ChessBoard.toDTO(p));
        return dto;
    }

    @GetMapping("/PGN_ALL")
    public String getPGN()
    {
        return chessBoard.allFormattedMoves;
    }

    @GetMapping("/PGN_THIS")
    public String getMovePGN() {
        return chessBoard.currentFormattedMove;
    }
}

