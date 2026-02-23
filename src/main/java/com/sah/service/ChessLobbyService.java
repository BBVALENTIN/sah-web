package com.sah.service;

import com.sah.dto.lobbyDTO;
import com.sah.entity.Chess_Games_Classic;
import com.sah.entity.Chess_Lobby;
import com.sah.enums.FormatType;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Service
public class ChessLobbyService {

    private final LobbyRepository lobbyRepository;
    private final Map<String, String> sessionLobbyMap = new ConcurrentHashMap<>();

    public static final String  lobbyIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-=";
    public static final SecureRandom random = new SecureRandom();
    public static final int length = 5;

    public ChessLobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public String GenerateRandomLobbyId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(lobbyIdPossibleCharacters.length());
            sb.append(lobbyIdPossibleCharacters.charAt(index));
        }

        return sb.toString();
    }

    public List<lobbyDTO> getAllDesiredLobbies(LobbyType typeOfLobby) {
        return lobbyRepository.findByLobbyType(typeOfLobby).stream().map(lobby -> new lobbyDTO(
                lobby.getLobbyId(),
                lobby.getLobbyType()
        )).toList();
    }

    public Chess_Lobby createLobby(LobbyType Type, String username) {
        Chess_Lobby newLobby = new Chess_Lobby();
        String randomLobbyId = GenerateRandomLobbyId();
        newLobby.setLobbyId(randomLobbyId);
        newLobby.setLobbyType(Type);
        newLobby.setFormat(FormatType.CLASSICAL);
        newLobby.setCreatedAt(LocalDateTime.now().withNano(0));
        assignLobbyPlayer(randomLobbyId, username);
        lobbyRepository.save(newLobby);
        return newLobby;
    }

    public Chess_Games_Classic createClassicalGame() {
        Chess_Games_Classic newClassicGame = new Chess_Games_Classic(); // to put the on
        return newClassicGame;
    }

    public void assignLobbyPlayer(String lobbyId, String username) {
        Chess_Lobby lobby = lobbyRepository.findByLobbyId(lobbyId);
        if(isLobbyEmpty(lobbyId) == true)
            throw new RuntimeException("Lobby is full");
        if(lobby.getPlayerWhite() == null && lobby.getPlayerBlack() == null) {
            if(ThreadLocalRandom.current().nextInt(1, 10) > 5) {
                lobby.setPlayerWhite(username);
            }
            else {
                lobby.setPlayerBlack(username);
            }
        }
        if(lobby.getPlayerBlack() == null) {
            lobby.setPlayerBlack(username);
        }
        else {
            lobby.setPlayerWhite(username);
        }
    }

    public boolean isLobbyEmpty(String lobbyId) {
        Chess_Lobby lobby = lobbyRepository.findByLobbyId(lobbyId);
        return lobby.getPlayerBlack() == null && lobby.getPlayerWhite() == null;
    }

    public void registerPlayer(String sessionId, String lobbyId) {
            sessionLobbyMap.put(sessionId, lobbyId);
    }

    public void handlePlayerDisconnect(String sessionId) {
        String lobbyId = sessionId;
    }
}
