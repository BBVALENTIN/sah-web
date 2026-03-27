package com.sah.service;

import com.sah.dto.GameDTO;
import com.sah.entity.ChessGamesClassic;
import com.sah.entity.ChessLobbies;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import org.springframework.stereotype.Service;

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

    public boolean isValidMoveForPlayer(String lobbyId, String username, ColorType culoarePiesa) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);

        if(culoarePiesa == ColorType.ALB) {
            return username.equals(lobby.getPlayerWhite());
        }
        else {
            return username.equals(lobby.getPlayerBlack());
        }
    }

    public void saveClassicGame(GameDTO dto) {
        ChessGamesClassic game =  new ChessGamesClassic();
        dto.setLobbyId(dto.getLobbyId());
        dto.setResult(dto.getResult());
        dto.setUserWhiteId(dto.getUserWhiteId());
        dto.setUserBlackId(dto.getUserBlackId());
        dto.setNumberOfMoves(dto.getNumberOfMoves());

        gameRepository.save(game);
    }

    // To implement further logic here
    public void removeLobby(String lobbyId){
        activeLobbies.remove(lobbyId);
    }
}
