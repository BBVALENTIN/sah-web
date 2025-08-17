package com.sah.controller;

import com.sah.game.ChessBoard;
import com.sah.game.dto.MoveResult;
import com.sah.game.piese.Piese;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessController {
    private final ChessBoard chessBoard = new ChessBoard();

    @PostMapping("/move")
    public MoveResult move(@RequestParam int fromRow,
                           @RequestParam int fromCol,
                           @RequestParam int toRow,
                           @RequestParam int toCol) {
        return chessBoard.faMiscare(fromRow, fromCol, toRow, toCol);
    }

    @GetMapping("/state")
    public List<Piese> getState() {
        return chessBoard.getAllPieces();
    }
}

