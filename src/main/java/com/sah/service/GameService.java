package com.sah.service;

import com.sah.dto.GameEndRequest;
import com.sah.dto.GameEndRequest;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.enums.LobbyType;
import com.sah.enums.Sides;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, ChessBoard> activeLobbies = new ConcurrentHashMap<>();
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;

    public GameService(LobbyRepository lobbyRepository, GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
    }

    public ChessBoard getOrCreateBoard(String lobbyId){
        return activeLobbies.computeIfAbsent(lobbyId, id -> {
            ChessBoard newBoard = new ChessBoard();
            newBoard.initializeBoard();
            return newBoard;
        });
    }

    public ChessBoard getBoard(String lobbyId) {
        return activeLobbies.get(lobbyId);
    }

    public boolean isValidMoveForPlayer(String lobbyId, String username, ColorType culoarePiesa) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);

        if(culoarePiesa == ColorType.ALB) {
            return username.equals(lobby.getPlayerWhite());
        }
        else {
            return username.equals(lobby.getPlayerBlack());
        }
    }

    public void saveClassicGame(String lobbyId) {
        ChessGames game =  new ChessGames();
        ChessBoard board = getBoard(lobbyId);
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);
        lobby.setLobbyType(LobbyType.FINISHED);
        game.setLobby(lobby);
        game.setResult(board.convertToResult());
        game.setPGN(board.getAllPGN());
        game.setNumberOfMoves(board.getMovesPlayed());
        game.setResignation(board.getResignation());

        gameRepository.save(game);
    }
    
    public void endGameEarly(GameEndRequest request)
    {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(request.lobbyId);
        if(lobby == null)
            throw new RuntimeException("Lobby doesn't exist");
        ChessBoard game = getBoard(request.lobbyId);
        if(!isUserInLobby(request))
            throw new RuntimeException("User is not in lobby");

        if(game.movesPlayed < 2)
            abortGame(lobby);
        else
            resignGame(lobby, game, request.principal);

        lobbyRepository.save(lobby);
    }

    private void abortGame(ChessLobbies lobby) {
        lobby.setLobbyType(LobbyType.ABORTED);
    }

    public void resignGame(ChessLobbies lobby, ChessBoard board, Principal principal) {
        board.setResignation(true);
        if(lobby.getPlayerWhite().equals(principal.getName()))
            board.setWinner(Sides.BLACK);
        else
            board.setWinner(Sides.WHITE);
        saveClassicGame(lobby.getLobbyId());
    }

    private boolean isUserInLobby(GameEndRequest request) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(request.lobbyId);
        if(lobby == null)
            return false;
        return lobby.getPlayerBlack().equals(request.principal.getName()) || lobby.getPlayerWhite().equals(request.principal.getName());
    }

    // To implement further logic here
    public void removeLobby(String lobbyId){
        activeLobbies.remove(lobbyId);
    }
}
