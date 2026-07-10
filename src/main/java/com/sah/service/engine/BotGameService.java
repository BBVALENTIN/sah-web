package com.sah.service.engine;

import com.sah.dto.chess.BotMinimalStateDTO;
import com.sah.dto.requests.BotMoveRequestDTO;
import com.sah.dto.responses.BotStartResponseDTO;
import com.sah.dto.responses.MoveResultDTO;
import com.sah.entity.BotGames;
import com.sah.entity.Users;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.ChessBoard;
import com.sah.game.gameenums.ColorType;
import com.sah.repository.BotGameRepository;
import com.sah.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotGameService {
    private record BotGameSession(ChessBoard board, String username, Sides botSide) {}
    private final Map<String, BotGameSession> botBoards = new ConcurrentHashMap<>();
    private final BotGameRepository botGameRepository;
    private final UserRepository userRepository;

    private static final String gameIdIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    private static final SecureRandom random = new SecureRandom();
    private static final int length = 5;

    public BotGameService(BotGameRepository botGameRepository, UserRepository userRepository)
    {
        this.botGameRepository = botGameRepository;
        this.userRepository = userRepository;
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
    public BotStartResponseDTO createBoard(String username, Sides botSide)
    {
        String gameId = "B_"+assignGameId();
        ChessBoard board = new ChessBoard();
        board.initializeBoard();
        botBoards.put(gameId, new BotGameSession(board, username, botSide));

        return new BotStartResponseDTO(gameId, board.getAllPiecesDTO());
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't move the chess engine's pieces");
        }

        return board.makeMove(req.getFromRow(), req.getFromCol(), req.getToRow(), req.getToCol());
    }

    private ColorType SideToColorConversion(Sides side)
    {
        if(side == Sides.WHITE)
            return ColorType.WHITE;
        else
            return ColorType.BLACK;
    }

    public void handleEndEarly(String gameId, String playerName) {
        ChessBoard board = getBoard(gameId);
        if(board.getMovesPlayed() >= 2)
            saveGame(gameId, true, playerName);
        else
            botBoards.remove(gameId);
    }

    public void saveGame(String gameId, boolean resignation, String playerName) {
        ChessBoard board = getBoard(gameId);
        BotGameSession session = getSession(gameId);
        Users player = userRepository.findByUsername(playerName);

        if(session == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session doesn't exist!");

        WinType winType = resignation ? WinType.RESIGNATION : WinType.CHECKMATE;
        ResultType result = board.convertToResult();
        BotGames botGame = new BotGames(gameId, player, session.botSide(), 18, result, winType, board.getMovesPlayed(), board.getAllPGN(), LocalDateTime.now());
        botGameRepository.save(botGame);
        botBoards.remove(gameId);
    }

    public BotMinimalStateDTO getMinimalStateDTO(String gameId) {
        BotGameSession session = getSession(gameId);
        if (session == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session doesn't exist!");

        ChessBoard board = session.board();
        return new BotMinimalStateDTO(
                board.getAllPiecesDTO(),
                board.getCurrentColor(),
                board.getAllPGN(),
                session.botSide(),
                board.getCurrentFen()
        );
    }
}
