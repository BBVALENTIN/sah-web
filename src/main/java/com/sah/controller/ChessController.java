package com.sah.controller;

import com.sah.dto.PiesaDTO;
import com.sah.game.ChessBoard;
import com.sah.dto.MoveResult;
import com.sah.game.piese.Piese;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessController {
    private final ChessBoard chessBoard = new ChessBoard();

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
        List<PiesaDTO> dto = new ArrayList<>();
        for (Piese p : chessBoard.getAllPieces()) {
            dto.add(ChessBoard.toDTO(p));
        }
        return dto;
    }

    @GetMapping("/turn")
    public int getCuloareCurenta()
    {
        return ChessBoard.getCuloareCurenta();
    }

    @GetMapping("/regeInamic")
    public Piese getPozitieRegeInamic()
    {
        return chessBoard.getRege(true);
    }

    @GetMapping("/regeTau")
    public Piese getPozitieRegeTau()
    {
        return chessBoard.getRege(false);
    }
}

