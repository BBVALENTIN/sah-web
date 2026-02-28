package com.sah.service;

import com.sah.game.ChessBoard;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, ChessBoard> activeLobbies = new ConcurrentHashMap<>();

    public ChessBoard getOrCreateBoard(String lobbyId){
        return activeLobbies.computeIfAbsent(lobbyId, id -> {
            System.out.println("Creating lobby " + lobbyId);
            ChessBoard newBoard = new ChessBoard();
            newBoard.initializeBoard();
            return newBoard;
        });
    }

    // To implement further logic here
    public void removeLobby(String lobbyId){
        activeLobbies.remove(lobbyId);
    }
}
