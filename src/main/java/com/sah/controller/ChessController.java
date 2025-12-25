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
    public PiesaDTO getPozitieRegeInamic()
    {
        return chessBoard.getRegeDTO(true);
    }

    @GetMapping("/regeTau")
    public PiesaDTO getPozitieRegeTau()
    {
        return chessBoard.getRegeDTO(false);
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

    @GetMapping("/check")
    public boolean isCheck(){
        Piese rege = chessBoard.getRege(false);
        return chessBoard.esteRegeInSah(rege);
    };

    @GetMapping("/checkmate")
    public boolean isCheckMate(){
        Piese rege = chessBoard.getRege(false);
        return chessBoard.esteSahMat(rege);
    }

    @GetMapping("/getMovesPGN")
    public String movesPGN()
    {
        return chessBoard.allFormatedMoves;
    }
}

