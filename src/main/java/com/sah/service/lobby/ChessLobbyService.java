package com.sah.service.lobby;

import com.sah.config.AppConstants;
import com.sah.dto.misc.LobbyDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.FormatType;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import com.sah.security.CurrentUserProvider;
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

    private static final String lobbyIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    private static final SecureRandom random = new SecureRandom();
    private final CurrentUserProvider currentUserProvider;

    public ChessLobbyService(LobbyRepository lobbyRepository, SimpMessagingTemplate simpMessagingTemplate, CurrentUserProvider currentUserProvider) {
        this.lobbyRepository = lobbyRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.currentUserProvider = currentUserProvider;
    }

    private String GenerateRandomLobbyId() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < AppConstants.LOBBY_CHARACTER_LIMIT; i++) {
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
        if (typeOfLobby == LobbyType.AVAILABLE) {
            return lobbyRepository.findByLobbyType(typeOfLobby).stream()
                    .map(this::convertLobbyDTO)
                    .toList();
        }
        return null;
    }

    public LobbyDTO getLobbyDTO(String lobbyId) {
        ChessLobbies lobby = getLobbyFromId(lobbyId);
        return convertLobbyDTO(lobby);
    }

    public String createLobby() {
        Users user = currentUserProvider.get();

        ChessLobbies lobby = new ChessLobbies();
        String randomLobbyId = assignLobbyId();
        lobby.setLobbyId(randomLobbyId);
        lobby.setLobbyType(LobbyType.AVAILABLE);
        lobby.setFormat(FormatType.CLASSICAL);
        assignLobbyPlayer(lobby, user);

        ChessLobbyChats chat = new ChessLobbyChats();
        lobby.setChat(chat);
        chat.setLobby(lobby);

        lobbyRepository.save(lobby);
        LobbyDTO lobbyDTO = convertLobbyDTO(lobby);
        simpMessagingTemplate.convertAndSend("/topic/global-lobbies", lobbyDTO);
        return lobby.getLobbyId();
    }

    public void joinLobby(String lobbyId) {
        ChessLobbies lobby = getLobbyFromId(lobbyId);
        Users currentUser = currentUserProvider.get();
        if(isAlreadyAssigned(lobby, currentUser) == false) {
            assignLobbyPlayer(lobby, currentUser);
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

    public LobbyDTO convertLobbyDTO(ChessLobbies lobby) {
        String playerWhiteUsername = lobby.getPlayerWhite() != null ? lobby.getPlayerWhite().getUsername() : null;
        String playerBlackUsername = lobby.getPlayerBlack() != null ? lobby.getPlayerBlack().getUsername() : null;

        return new LobbyDTO(lobby.getLobbyId(), lobby.getLobbyType(), playerWhiteUsername, playerBlackUsername);
    }

    public void assignLobbyPlayer(ChessLobbies lobby, Users user) {
        if(isLobbyFull(lobby)) {
            throw new RuntimeException("Lobby is full or player is already assigned");
        }
        if(lobby.getPlayerWhite() == null && lobby.getPlayerBlack() == null) {
            if(ThreadLocalRandom.current().nextInt(1, 10) > 5) {
                lobby.setPlayerWhite(user);
            }
            else {
                lobby.setPlayerBlack(user);
            }
        }
        else {
            if (lobby.getPlayerBlack() == null) {
                lobby.setPlayerBlack(user);
            } else {
                lobby.setPlayerWhite(user);
            }
        }
    }

    private boolean isAlreadyAssigned(ChessLobbies lobby, Users user) {
        return Objects.equals(lobby.getPlayerBlack(), user) || Objects.equals(lobby.getPlayerWhite(), user);
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
