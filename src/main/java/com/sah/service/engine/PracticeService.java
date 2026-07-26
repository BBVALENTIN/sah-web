package com.sah.service.engine;

import com.sah.dto.chess.MinimalStateDTO;
import com.sah.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PracticeService {
    @Autowired
    private Game game;

    public MinimalStateDTO initialize() {
        game.initializeBoard();

        MinimalStateDTO minimalStateDTO = new MinimalStateDTO(game.getAllPiecesDTO(), game.currentColor, "");

        return minimalStateDTO;
    }
}
