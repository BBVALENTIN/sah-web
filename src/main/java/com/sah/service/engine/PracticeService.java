package com.sah.service.engine;

import com.sah.dto.chess.MinimalStateDTO;
import com.sah.game.Game;
import com.sah.service.chess.GameService;
import org.springframework.stereotype.Service;

@Service
public class PracticeService {

    private final GameService gameService;

    public PracticeService(GameService gameService)
    {
        this.gameService = gameService;
    }

    public MinimalStateDTO initialize() {
        gameService.newPracticeGame();
        Game game = gameService.getPracticeGame();

        return new MinimalStateDTO(game.getCurrentFEN(), game.getFullPGN());
    }
}
