package com.sah.service;

import com.sah.dto.MinimalStateDTO;
import com.sah.game.ChessBoard;
import org.springframework.stereotype.Service;

@Service
public class PracticeService {

    public MinimalStateDTO initialize() {
        ChessBoard practiceBoard =  new ChessBoard();
        practiceBoard.initializeBoard();

        MinimalStateDTO minimalStateDTO = new MinimalStateDTO(practiceBoard.getAllPiecesDTO(), practiceBoard.culoareCurenta, practiceBoard.getAllPGN());

        return minimalStateDTO;
    }
}
