package com.sah.service;

import com.sah.dto.misc.LobbyDTO;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChats;
import com.sah.enums.FormatType;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import com.sah.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ChessLobbyService {

    private final LobbyRepository lobbyRepository;
    private final Map<String, String> sessionLobbyMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChessLobbyChatService chessLobbyChatService;
    private final UserRepository userRepository;

    private static final String lobbyIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    private static final SecureRandom random = new SecureRandom();
    private static final int length = 5;

    public ChessLobbyService(LobbyRepository lobbyRepository,  SimpMessagingTemplate simpMessagingTemplate, ChessLobbyChatService chessLobbyChatService, UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chessLobbyChatService = chessLobbyChatService;
        this.userRepository = userRepository;
    }

    private String GenerateRandomLobbyId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(lobbyIdPossibleCharacters.length());
            sb.append(lobbyIdPossibleCharacters.charAt(index));
        }

        return sb.toString();
    }

    private String assignLobbyId() {
        String lobbyId;
        do {
            lobbyId = GenerateRandomLobbyId();
        } while (lobbyRepository.findByLobbyId(lobbyId) != null);
        return lobbyId;
    }

    public List<LobbyDTO> getAllDesiredLobbies(LobbyType typeOfLobby) {
        if(typeOfLobby == LobbyType.AVAILABLE) {
            return lobbyRepository.findByLobbyType(typeOfLobby).stream().map(lobby -> new LobbyDTO(
                    lobby.getLobbyId(),
                    lobby.getLobbyType(),
                    lobby.getPlayerWhite(),
                    lobby.getPlayerBlack()
            )).toList();
        }
        return null;
    }

    public LobbyDTO getLobbyDTO(String lobbyId) {
        ChessLobbies lobby = getLobbyFromId(lobbyId);
        return new LobbyDTO(lobbyId, lobby.getLobbyType(), lobby.getPlayerWhite(), lobby.getPlayerBlack());
    }

    // IMPORTANT LOBBY STUFF
    public LobbyDTO createLobby(String username) {
        ChessLobbies lobby = new ChessLobbies();
        String randomLobbyId = assignLobbyId();
        lobby.setLobbyId(randomLobbyId);
        lobby.setLobbyType(LobbyType.AVAILABLE);
        lobby.setFormat(FormatType.CLASSICAL);
        assignLobbyPlayer(lobby, username);

        ChessLobbyChats chat = new ChessLobbyChats();
        lobby.setChat(chat);
        chat.setLobby(lobby);

        lobbyRepository.save(lobby);
        LobbyDTO lobbyDTO = convertLobbyDTO(lobby);
        simpMessagingTemplate.convertAndSend("/topic/global-lobbies", lobbyDTO);
        return lobbyDTO;
    }

    public void joinLobby(String lobbyId, String username) {
        ChessLobbies lobby = getLobbyFromId(lobbyId);
        if(isAlreadyAssigned(lobby, username) == false) {
            assignLobbyPlayer(lobby, username);
            if(isLobbyFull(lobby)) {
                lobby.setLobbyType(LobbyType.ONGOING);
                LobbyDTO lobbyDTO = convertLobbyDTO(lobby);
                simpMessagingTemplate.convertAndSend("/topic/global-lobbies", lobbyDTO);
            }
            lobbyRepository.save(lobby);
            updatedLobbyNotify(lobbyId);
        }
    }

    private void updatedLobbyNotify(String lobbyId) {
        LobbyDTO updatedLobbyDTO = getLobbyDTO(lobbyId);
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, updatedLobbyDTO);
    }

    public ChessGames createClassicalGame() {
        ChessGames newClassicGame = new ChessGames(); // to put the on
        return newClassicGame;
    }

    public LobbyDTO convertLobbyDTO(ChessLobbies lobby) {
        LobbyDTO lobbyDTO = new LobbyDTO(lobby.getLobbyId(), lobby.getLobbyType(), lobby.getPlayerWhite(), lobby.getPlayerBlack());
        return lobbyDTO;
    }

    public void assignLobbyPlayer(ChessLobbies lobby, String username) {
        // TO ASSIGN IF THE PLAYER IS ALREADY CONNECTED
        if(isLobbyFull(lobby)) {
            throw new RuntimeException("Lobby is full or player is already assigned");
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

    private boolean isAlreadyAssigned(ChessLobbies lobby, String username) {
        return Objects.equals(lobby.getPlayerBlack(), username) || Objects.equals(lobby.getPlayerWhite(), username);
    }

    // to be used
    public boolean isLobbyEmpty(ChessLobbies lobby) {
        return lobby.getPlayerBlack() == null && lobby.getPlayerWhite() == null;
    }

    public boolean isLobbyFull(ChessLobbies lobby) {
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

    public ChessLobbies getLobbyFromId(String lobbyId) {
        return lobbyRepository.findByLobbyId(lobbyId);
    }
}
