package com.sah.controller;

import com.sah.dto.BotMoveRequestDTO;
import com.sah.dto.BotStartResponseDTO;
import com.sah.dto.MoveRequestDTO;
import com.sah.dto.MoveResultDTO;
import com.sah.enums.Sides;
import com.sah.game.ChessBoard;
import com.sah.service.BotGameService;
import com.sah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/bot")
public class BotGameController {
    private final ChessBoard chessBoard;
    private final BotGameService botGameService;

    @Autowired
    public BotGameController(ChessBoard chessBoard, BotGameService botGameService)
    {
        this.chessBoard = chessBoard;
        this.botGameService = botGameService;
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestBody BotMoveRequestDTO req) {
        ChessBoard board = botGameService.getBoard(req.getGameId());
        if(board == null) return ResponseEntity.notFound().build();

        MoveResultDTO result = botGameService.makeMove(req);
        if(result.getErrorCodes() != null) {
            return ResponseEntity.badRequest().body(result.getErrorCodes());
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/bot-move")
    public ResponseEntity<?> botMove(@RequestBody BotMoveRequestDTO req) {
        ChessBoard board = botGameService.getBoard(req.getGameId());
        if (board == null) return ResponseEntity.notFound().build();

        MoveResultDTO result = board.faMiscare(req.getFromRow(), req.getFromCol(), req.getToRow(), req.getToCol());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestParam Sides botSide, Principal principal)
    {
        BotStartResponseDTO response = botGameService.getOrCreateBoard(principal.getName(), botSide);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/end/{gameId}")
//    public ResponseEntity<?> endGame(@PathVariable Long gameId, Principal principal)
//    {
//
//    }
//
//    @GetMapping("/state")
//    public ResponseEntity<?> getState() {
//
//    }
}
