package com.sah.controller.api;

import com.sah.dto.requests.BotMoveRequestDTO;
import com.sah.dto.responses.BotStartResponseDTO;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.Game;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.repository.UserRepository;
import com.sah.service.engine.BotGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot")
public class BotGameApiController {
    private final BotGameService botGameService;

    @Autowired
    public BotGameApiController(BotGameService botGameService, UserRepository userRepository)
    {
        this.botGameService = botGameService;
    }

    @PostMapping("/move") // the endpoint user calls when playing a bot game, maybe redundant
    public ResponseEntity<?> move(@RequestBody BotMoveRequestDTO req) throws InvalidMoveException {
        Game board = botGameService.getBoard(req.getGameId());
        if(board == null) return ResponseEntity.notFound().build();

        try {
            OMoveResult result = botGameService.makeMove(req);
            return ResponseEntity.ok(result);
        }
        catch(InvalidMoveException ex)
        {
            return ResponseEntity.badRequest().body(ex.getErrorCodes());
        }
    }

    @PostMapping("/bot-move") // The endpoint stockfish calls
    public ResponseEntity<?> botMove(@RequestBody BotMoveRequestDTO req) throws InvalidMoveException {
        Game game = botGameService.getBoard(req.getGameId());
        if (game == null) return ResponseEntity.notFound().build();

        try {
            OMoveResult result = game.makeMove(req.getMoveCoords());
            if(result.isCheckMate())
            {
                botGameService.saveGame(req.getGameId(), WinType.CHECKMATE, ResultType.BLACK_WIN); // maybe buggy here CHANGE
            }
            return ResponseEntity.ok(result);
        }
        catch (InvalidMoveException ex) {
            return ResponseEntity.badRequest().body(ex.getErrorCodes());
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestParam Sides botSide)
    {
        BotStartResponseDTO response = botGameService.createBoard(botSide);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/end/{gameId}")
    public ResponseEntity<?> endGame(@PathVariable String gameId) // resign endpoint
    {
        String result = botGameService.handleEndEarly(gameId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/state/{gameId}")
    public ResponseEntity<?> getState(@PathVariable String gameId) {
        return ResponseEntity.ok(botGameService.getMinimalStateDTO(gameId));
    }
}
