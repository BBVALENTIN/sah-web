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
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class ChessLobbyService {

    private final LobbyRepository lobbyRepository;

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

    public Stream<lobbyDTO> getAllDesiredLobbies(LobbyType typeOfLobby) {
        return lobbyRepository.findByLobbyType(typeOfLobby).stream().map(lobby -> new lobbyDTO(
                lobby.getLobbyId(),
                lobby.getLobbyType()
        ));
    }

    public Chess_Lobby createLobby(LobbyType Type) {
        Chess_Lobby newLobby = new Chess_Lobby();
        String randomLobbyId = GenerateRandomLobbyId();
        newLobby.setLobbyId(randomLobbyId);
        newLobby.setLobbyType(Type);
        newLobby.setFormat(FormatType.CLASSICAL);
        newLobby.setCreatedAt(LocalDateTime.now().withNano(0));

        return newLobby;
    }

    public Chess_Games_Classic createClassicalGame() {
        Chess_Games_Classic newClassicGame = new Chess_Games_Classic(); // to put the on
        return newClassicGame;
    }

    //Implement save in the database here
    public void saveDb() {

    }
}
