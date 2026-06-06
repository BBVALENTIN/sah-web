package com.sah.controller.api;

import com.sah.dto.requests.BotMoveRequestDTO;
import com.sah.dto.responses.BotStartResponseDTO;
import com.sah.dto.responses.MoveResultDTO;
import com.sah.enums.Sides;
import com.sah.game.ChessBoard;
import com.sah.service.BotGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/bot")
public class BotGameApiController {
    private final ChessBoard chessBoard;
    private final BotGameService botGameService;

    @Autowired
    public BotGameApiController(ChessBoard chessBoard, BotGameService botGameService)
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
    public ResponseEntity<?> botMove(@RequestBody BotMoveRequestDTO req, Principal principal) {
        ChessBoard board = botGameService.getBoard(req.getGameId());
        if (board == null) return ResponseEntity.notFound().build();

        MoveResultDTO result = board.faMiscare(req.getFromRow(), req.getFromCol(), req.getToRow(), req.getToCol());
        if(result.isCheckmate())
        {
            botGameService.saveGame(req.getGameId(), false, principal.getName());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestParam Sides botSide, Principal principal)
    {
        BotStartResponseDTO response = botGameService.createBoard(principal.getName(), botSide);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/end/{gameId}")
    public ResponseEntity<?> endGame(@PathVariable String gameId, Principal principal) // resign endpoint
    {
        botGameService.saveGame(gameId, true, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/state/{gameId}")
    public ResponseEntity<?> getState(@PathVariable String gameId) {
        return ResponseEntity.ok(botGameService.getMinimalStateDTO(gameId));
    }
}
