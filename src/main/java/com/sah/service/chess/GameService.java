package com.sah.service.chess;

import com.sah.dto.requests.GameEndRequest;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
import com.sah.enums.ResultType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.Game;
import com.sah.game.gameenums.ColorType;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, Game> activeLobbies = new ConcurrentHashMap<>();
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private Game practiceGame;


    public GameService(LobbyRepository lobbyRepository, GameRepository gameRepository, SimpMessageSendingOperations messageTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.messageTemplate = messageTemplate;
    }


    public Game getPracticeGame() {
        if(practiceGame == null) {
            practiceGame = new Game();
        }
        return practiceGame;
    }

    public void newPracticeGame() {
        this.practiceGame = new Game();
    }

    public Game getOrCreateBoard(String lobbyId){
        return activeLobbies.computeIfAbsent(lobbyId, id -> {
            Game newBoard = new Game();
            return newBoard;
        });
    }

    public Game getBoard(String lobbyId) {
        return activeLobbies.get(lobbyId);
    }

    public boolean isValidMoveForPlayer(String lobbyId, Users user, ColorType pieceColor) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);

        if(pieceColor == ColorType.WHITE) {
            return lobby.getPlayerWhite() != null && user.getUserId().equals(lobby.getPlayerWhite().getUserId());
        }
        else {
            return lobby.getPlayerBlack() != null && user.getUserId().equals(lobby.getPlayerBlack().getUserId());
        }
    }

    public void saveClassicGame(String lobbyId, WinType winReason, Sides winner) {
        ChessGames ChessGameDB =  new ChessGames();
        Game game = getBoard(lobbyId);
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);
        lobby.setLobbyType(LobbyType.FINISHED);
        ChessGameDB.setLobby(lobby);
        ChessGameDB.setWinReason(winReason);
        ChessGameDB.setResult(convertToResult(winner));
        ChessGameDB.setPGN(game.getFullPGN());
        ChessGameDB.setNumberOfMoves(game.getFullMove());

        gameRepository.save(ChessGameDB);

        activeLobbies.remove(lobbyId);
    }

    public void endGameEarly(GameEndRequest request)
    {
        Sides winner = Sides.NONE;
        ChessLobbies lobby = lobbyRepository.findByLobbyId(request.lobbyId);
        if(lobby == null)
            throw new RuntimeException("Lobby doesn't exist");
        Game game = getBoard(request.lobbyId);
        if(!isUserInLobby(request))
            throw new RuntimeException("User is not in lobby");

        if(game.getFullMove() < 2)
            abortGame(lobby);
        else
            winner = resignGame(lobby, request.currentUser);
        messageTemplate.convertAndSend("/topic/resign-lobby/" + lobby.getLobbyId(), winner);
        lobbyRepository.save(lobby);
    }

    private void abortGame(ChessLobbies lobby) {
        lobby.setLobbyType(LobbyType.ABORTED);
    }

    public Sides resignGame(ChessLobbies lobby, Users currentUser) {
        Sides winner = Sides.NONE;
        if(lobby.getPlayerWhite().getUsername().equals(currentUser.getUsername()))
            winner = Sides.BLACK;
        else
            winner = Sides.WHITE;
        saveClassicGame(lobby.getLobbyId(), WinType.RESIGNATION, winner);
        return winner;
    }

    private boolean isUserInLobby(GameEndRequest request) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(request.lobbyId);
        if(lobby == null)
            return false;
        return lobby.getPlayerBlack().getUsername().equals(request.currentUser.getUsername()) || lobby.getPlayerWhite().getUsername().equals(request.currentUser.getUsername());
    }

    private ResultType convertToResult(Sides winner) {
        return winner == Sides.WHITE ? ResultType.WHITE_WIN : ResultType.BLACK_WIN;
    }

    // To implement further logic here
    public void removeLobby(String lobbyId){
        activeLobbies.remove(lobbyId);
    }
}
