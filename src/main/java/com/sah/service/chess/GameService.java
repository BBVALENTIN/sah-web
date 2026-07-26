package com.sah.service.chess;

import com.sah.dto.requests.GameEndRequest;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
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


    public GameService(LobbyRepository lobbyRepository, GameRepository gameRepository, SimpMessageSendingOperations messageTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.messageTemplate = messageTemplate;
    }

    public Game getOrCreateBoard(String lobbyId){
        return activeLobbies.computeIfAbsent(lobbyId, id -> {
            Game newBoard = new Game();
            newBoard.initializeBoard();
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

    public void saveClassicGame(String lobbyId, WinType winReason) {
        ChessGames game =  new ChessGames();
        Game board = getBoard(lobbyId);
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);
        lobby.setLobbyType(LobbyType.FINISHED);
        game.setLobby(lobby);
        game.setResult(board.convertToResult());
        game.setPGN(board.getAllPGN());
        game.setNumberOfMoves(board.getMovesPlayed());
        game.setWinReason(winReason);

        gameRepository.save(game);
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

        if(game.movesPlayed < 2)
            abortGame(lobby);
        else
            winner = resignGame(lobby, game, request.currentUser);
        messageTemplate.convertAndSend("/topic/resign-lobby/" + lobby.getLobbyId(), winner);
        lobbyRepository.save(lobby);
    }

    private void abortGame(ChessLobbies lobby) {
        lobby.setLobbyType(LobbyType.ABORTED);
    }

    public Sides resignGame(ChessLobbies lobby, Game board, Users currentUser) {
        board.setResignation(true);
        if(lobby.getPlayerWhite().getUsername().equals(currentUser.getUsername()))
            board.setWinner(Sides.BLACK);
        else
            board.setWinner(Sides.WHITE);
        saveClassicGame(lobby.getLobbyId(), WinType.RESIGNATION);
        return board.getWinner();
    }

    private boolean isUserInLobby(GameEndRequest request) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(request.lobbyId);
        if(lobby == null)
            return false;
        return lobby.getPlayerBlack().getUsername().equals(request.currentUser.getUsername()) || lobby.getPlayerWhite().getUsername().equals(request.currentUser.getUsername());
    }

    // To implement further logic here
    public void removeLobby(String lobbyId){
        activeLobbies.remove(lobbyId);
    }
}
