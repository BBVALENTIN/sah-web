package com.sah.service.engine;

import com.sah.config.AppConstants;
import com.sah.dto.chess.BotMinimalStateDTO;
import com.sah.dto.requests.BotMoveRequestDTO;
import com.sah.dto.responses.BotStartResponseDTO;
import com.sah.entity.BotGames;
import com.sah.entity.Users;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.Game;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.gameenums.ColorType;
import com.sah.repository.BotGameRepository;
import com.sah.security.CurrentUserProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotGameService {
    private record BotGameSession(Game board, String username, Sides botSide) {}
    private final Map<String, BotGameSession> botBoards = new ConcurrentHashMap<>();
    private final BotGameRepository botGameRepository;
    private final CurrentUserProvider currentUserProvider;

    private static final String gameIdIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    private static final SecureRandom random = new SecureRandom();
    private static final int length = 5;

    public BotGameService(BotGameRepository botGameRepository, CurrentUserProvider currentUserProvider)
    {
        this.botGameRepository = botGameRepository;
        this.currentUserProvider = currentUserProvider;
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
    public BotStartResponseDTO createBoard(Sides botSide)
    {
        String gameId = "B_"+assignGameId();
        Game board = new Game();
        Users currentUser = currentUserProvider.get();
        board.initializeBoard();
        botBoards.put(gameId, new BotGameSession(board, currentUser.getUsername(), botSide));

        return new BotStartResponseDTO(gameId, board.getAllPiecesDTO());
    }

    public Game getBoard(String gameId)
    {
        BotGameSession session = botBoards.get(gameId);
        if(session == null) return null;
        return session.board();
    }

    private BotGameSession getSession(String gameId) {
        return botBoards.get(gameId);
    }

    public OMoveResult makeMove(BotMoveRequestDTO req) throws InvalidMoveException {
        Game board = getBoard(req.getGameId());
        BotGameSession session = getSession(req.getGameId());


        Sides playerSide = session.botSide().equals(Sides.WHITE) ? Sides.BLACK : Sides.WHITE;
        ColorType pieceColor = board.board[req.getMoveCoords().getFromRow()][req.getMoveCoords().getFromCol()].color;

        if (pieceColor != SideToColorConversion(playerSide)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't move the chess engine's pieces");
        }

        return board.makeOptimisedMove(req.getMoveCoords());
    }

    private ColorType SideToColorConversion(Sides side)
    {
        if(side == Sides.WHITE)
            return ColorType.WHITE;
        else
            return ColorType.BLACK;
    }

    public String handleEndEarly(String gameId) {
        Game board = getBoard(gameId);

        if(board == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found!");
        }

        if(board.getMovesPlayed() >= 2) {
            return convertResultToWinString(resignGame(gameId));
        }
        else {
            return convertResultToWinString(abortGame(gameId));
        }
    }

    private String convertResultToWinString(ResultType resultType) {
        return switch (resultType) {
            case WHITE_WIN -> AppConstants.strWHITE_WIN;
            case BLACK_WIN -> AppConstants.strBLACK_WIN;
            case DRAW -> AppConstants.strDRAW;
            case ABORTED -> AppConstants.strABORTED;
        };
    }

    private ResultType resignGame(String gameId) {
        BotGameSession session = getSession(gameId);

        if(session == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session doesn't exist!");
        }

        if(session.botSide == Sides.WHITE)
        {
            saveGame(gameId, WinType.RESIGNATION, ResultType.WHITE_WIN);
            return ResultType.WHITE_WIN;
        }
        else {
            saveGame(gameId, WinType.RESIGNATION, ResultType.BLACK_WIN);
            return ResultType.BLACK_WIN;
        }
    } // function made like this because the engine can't resign

    private ResultType abortGame(String gameId) {
        botBoards.remove(gameId);
        return ResultType.ABORTED;
    }

    public void saveGame(String gameId, WinType winType, ResultType result) {
        Game board = getBoard(gameId);
        BotGameSession session = getSession(gameId);
        Users player = currentUserProvider.get();

        if(session == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session doesn't exist!");

        BotGames botGame = BotGames.builder()
                .gameId(gameId)
                .player(player)
                .botSide(session.botSide)
                .stockfishDepth(18) // change this
                .result(result)
                .winReason(winType)
                .numberOfMoves(board.getMovesPlayed())
                .PGN(board.getAllPGN())
                .playedAt(LocalDateTime.now())
                .build();

        botGameRepository.save(botGame);
        botBoards.remove(gameId);
    }

    public BotMinimalStateDTO getMinimalStateDTO(String gameId) {
        BotGameSession session = getSession(gameId);
        if (session == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session doesn't exist!");

        Game board = session.board();
        return new BotMinimalStateDTO(
                board.getAllPiecesDTO(),
                board.getCurrentColor(),
                board.getAllPGN(),
                session.botSide(),
                board.getCurrentFen()
        );
    }
}
