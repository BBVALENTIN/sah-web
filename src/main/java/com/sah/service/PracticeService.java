package com.sah.service;

import com.sah.dto.chess.MinimalStateDTO;
import com.sah.game.ChessBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PracticeService {
    @Autowired
    private ChessBoard chessBoard;

    public MinimalStateDTO initialize() {
        chessBoard.initializeBoard();

        MinimalStateDTO minimalStateDTO = new MinimalStateDTO(chessBoard.getAllPiecesDTO(), chessBoard.culoareCurenta, "");

        return minimalStateDTO;
    }
}
