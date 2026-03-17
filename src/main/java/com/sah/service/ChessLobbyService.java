package com.sah.service;

import com.sah.dto.JoinLobbyRequest;
import com.sah.dto.LobbyDTO;
import com.sah.entity.ChessGamesClassic;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.FormatType;
import com.sah.enums.MessageType;
import com.sah.repository.ChessLobbyChatRepository;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import com.sah.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
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
    private final ChessLobbyChatRepository lobbyChatRepository;
    private final UserRepository userRepository;

    public static final String lobbyIdPossibleCharacters = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ+-";
    public static final SecureRandom random = new SecureRandom();
    public static final int length = 5;

    public ChessLobbyService(LobbyRepository lobbyRepository,  SimpMessagingTemplate simpMessagingTemplate, ChessLobbyChatService chessLobbyChatService, ChessLobbyChatRepository lobbyChatRepository, UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chessLobbyChatService = chessLobbyChatService;
        this.lobbyChatRepository = lobbyChatRepository;
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
        return lobbyRepository.findByLobbyType(typeOfLobby).stream().map(lobby -> new LobbyDTO(
                lobby.getLobbyId(),
                lobby.getLobbyType()
        )).toList();
    }

    public LobbyDTO getLobbyDTO(String lobbyId) {
        ChessLobbies lobby = getLobbyFromId(lobbyId);
        return new LobbyDTO(lobbyId, lobby.getLobbyType(), lobby.getPlayerWhite(), lobby.getPlayerBlack());
    }

    // IMPORTANT LOBBY STUFF
    public ChessLobbies createLobby(String username) {
        ChessLobbies lobby = new ChessLobbies();
        String randomLobbyId = assignLobbyId();
        lobby.setLobbyId(randomLobbyId);
        lobby.setLobbyType(LobbyType.AVAILABLE);
        lobby.setFormat(FormatType.CLASSICAL);
        lobby.setCreatedAt(LocalDateTime.now().withNano(0));
        assignLobbyPlayer(lobby, username);

        ChessLobbyChats chat = new ChessLobbyChats();
        chat.setChatId(chessLobbyChatService.assignLobbyChatId());
        lobby.setChat(chat);

        lobbyRepository.save(lobby);

        return lobby;
    }

    public void joinLobby(JoinLobbyRequest request) {
        ChessLobbies lobby = getLobbyFromId(request.getLobbyId());
        if(isAlreadyAssigned(lobby, request.getUsername()) == false) {
            assignLobbyPlayer(lobby, request.getUsername());
            if(isLobbyFull(lobby))
                lobby.setLobbyType(LobbyType.ONGOING);
            lobbyRepository.save(lobby);
            updatedLobbyNotify(request.getLobbyId());
        }
    }

    private void updatedLobbyNotify(String lobbyId) {
        LobbyDTO updatedLobbyDTO = getLobbyDTO(lobbyId);
        simpMessagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, updatedLobbyDTO);
    }

    public ChessGamesClassic createClassicalGame() {
        ChessGamesClassic newClassicGame = new ChessGamesClassic(); // to put the on
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
