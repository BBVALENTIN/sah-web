package com.sah.service;

import com.sah.dto.lobbyDTO;
import com.sah.repository.LobbyRepository;
import com.sah.enums.LobbyType;
import jakarta.persistence.Lob;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    public Stream<lobbyDTO> getAllAvailablesLobbies() {
        return lobbyRepository.findByTip(LobbyType.AVAILABLE).stream().map(lobby -> new lobbyDTO(
                lobby.getLobby_Id(),
                lobby.getLobbyType()
        ));
    }
}
