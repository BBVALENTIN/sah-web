package com.sah.service;

import com.sah.dto.BotMoveRequestDTO;
import com.sah.dto.BotStartResponseDTO;
import com.sah.dto.MoveResultDTO;
import com.sah.entity.BotGames;
import com.sah.enums.Sides;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.repository.BotGameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotGameService {
    private record BotGameSession(ChessBoard board, Sides botSide) {}
    private final Map<String, BotGameSession> botBoards = new ConcurrentHashMap<>();
    private final BotGameRepository botGameRepository;

    private static final String gameIdIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    private static final SecureRandom random = new SecureRandom();
    private static final int length = 5;

    public BotGameService(BotGameRepository botGameRepository)
    {
        this.botGameRepository = botGameRepository;
    }

    private String GenerateRandomGameId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(gameIdIdPossibleCharacters.length());
            sb.append(gameIdIdPossibleCharacters.charAt(index));
        }

        return sb.toString();
    }

    private String assignGameId() {
        String gameId;
        do {
            gameId = GenerateRandomGameId();
        } while (botGameRepository.findByGameId(gameId) != null);
        return gameId;
    }
    public BotStartResponseDTO getOrCreateBoard(String username, Sides botSide)
    {
        String gameId = "B_"+assignGameId();
        ChessBoard board = new ChessBoard();
        board.initializeBoard();
        botBoards.put(gameId, new BotGameSession(board, botSide));

        return new BotStartResponseDTO(gameId, board);
    }

    public ChessBoard getBoard(String gameId)
    {
        BotGameSession session = botBoards.get(gameId);
        if(session == null) return null;
        return session.board();
    }

    public BotGameSession getSession(String gameId) {
        return botBoards.get(gameId);
    }

    public MoveResultDTO makeMove(BotMoveRequestDTO req) {
        ChessBoard board = getBoard(req.getGameId());
        BotGameSession session = getSession(req.getGameId());

        Sides playerSide = session.botSide().equals(Sides.WHITE) ? Sides.BLACK : Sides.WHITE;
        ColorType pieceColor = board.board[req.getFromRow()][req.getFromCol()].color;

        if (pieceColor != SideToColorConversion(playerSide)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nu poti muta piesele botului!");
        }

        return board.faMiscare(req.getFromRow(), req.getFromCol(), req.getToRow(), req.getToCol());
    }

    private ColorType SideToColorConversion(Sides side)
    {
        if(side == Sides.WHITE)
            return ColorType.ALB;
        else
            return ColorType.NEGRU;
    }
}
