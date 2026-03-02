package com.sah.service;

import com.sah.dto.JoinLobbyRequest;
import com.sah.dto.LobbyDTO;
import com.sah.entity.Chess_Games_Classic;
import com.sah.entity.Chess_Lobby;
import com.sah.enums.FormatType;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import jakarta.persistence.Lob;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ChessLobbyService {

    private final LobbyRepository lobbyRepository;
    private final Map<String, String> sessionLobbyMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;

    public static final String  lobbyIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-=";
    public static final SecureRandom random = new SecureRandom();
    public static final int length = 5;

    public ChessLobbyService(LobbyRepository lobbyRepository,  SimpMessagingTemplate simpMessagingTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public String GenerateRandomLobbyId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(lobbyIdPossibleCharacters.length());
            sb.append(lobbyIdPossibleCharacters.charAt(index));
        }

        return sb.toString();
    }

    public List<LobbyDTO> getAllDesiredLobbies(LobbyType typeOfLobby) {
        return lobbyRepository.findByLobbyType(typeOfLobby).stream().map(lobby -> new LobbyDTO(
                lobby.getLobbyId(),
                lobby.getLobbyType()
        )).toList();
    }

    public LobbyDTO getLobbyDTO(String lobbyId) {
        Chess_Lobby lobby = getLobbyFromId(lobbyId);
        return new LobbyDTO(lobbyId, lobby.getLobbyType(), lobby.getPlayerWhite(), lobby.getPlayerBlack());
    }

    // IMPORTANT LOBBY STUFF
    public Chess_Lobby createLobby(String username) {
        Chess_Lobby lobby = new Chess_Lobby();
        String randomLobbyId = GenerateRandomLobbyId();
        lobby.setLobbyId(randomLobbyId);
        lobby.setLobbyType(LobbyType.AVAILABLE);
        lobby.setFormat(FormatType.CLASSICAL);
        lobby.setCreatedAt(LocalDateTime.now().withNano(0));
        assignLobbyPlayer(lobby, username);
        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(JoinLobbyRequest request) {
        Chess_Lobby lobby = getLobbyFromId(request.getLobbyId());
        assignLobbyPlayer(lobby, request.getUsername());
        lobbyRepository.save(lobby);
        updatedLobbyNotify(request.getLobbyId());
    }

    public void updatedLobbyNotify(String lobbyId) {
        LobbyDTO updatedLobbyDTO = getLobbyDTO(lobbyId);
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, updatedLobbyDTO);
        System.out.println("JHADEHGIDFHGAIL5432096092436== = 6=5 4-6- - UPDATED LOBBY" + updatedLobbyDTO.playerBlack +  updatedLobbyDTO.playerWhite);
    }

    public Chess_Games_Classic createClassicalGame() {
        Chess_Games_Classic newClassicGame = new Chess_Games_Classic(); // to put the on
        return newClassicGame;
    }

    public LobbyDTO convertLobbyDTO(Chess_Lobby lobby) {
        LobbyDTO lobbyDTO = new LobbyDTO(lobby.getLobbyId(), lobby.getLobbyType(), lobby.getPlayerWhite(), lobby.getPlayerBlack());
        return lobbyDTO;
    }
    public void assignLobbyPlayer(Chess_Lobby lobby, String username) {
        // TO ASSIGN IF THE PLAYER IS ALREADY CONNECTED
        if(isLobbyFull(lobby)) {
            throw new RuntimeException("Lobby is full");
        }
        if(lobby.getPlayerWhite() == null && lobby.getPlayerBlack() == null) {
            if(ThreadLocalRandom.current().nextInt(1, 10) > 5) {
                lobby.setPlayerWhite(username);
            }
            else {
                lobby.setPlayerBlack(username);
            }
        }
        else {
            if (lobby.getPlayerBlack() == null) {
                lobby.setPlayerBlack(username);
            } else {
                lobby.setPlayerWhite(username);
            }
        }
    }

    // to be used
    public boolean isLobbyEmpty(Chess_Lobby lobby) {
        return lobby.getPlayerBlack() == null && lobby.getPlayerWhite() == null;
    }

    public boolean isLobbyFull(Chess_Lobby lobby) {
        return lobby.getPlayerWhite() != null && lobby.getPlayerBlack() != null;
    }

    public void registerPlayer(String sessionId, String lobbyId) {
            sessionLobbyMap.put(sessionId, lobbyId);
    }

    public String getLobbyIdFromSession(String sessionId) {
        return sessionLobbyMap.get(sessionId);
    }

    public void handlePlayerDisconnect(String sessionId) {
        String lobbyId = sessionId;
    }

    public Chess_Lobby getLobbyFromId(String lobbyId) {
        return lobbyRepository.findByLobbyId(lobbyId);
    }
}
