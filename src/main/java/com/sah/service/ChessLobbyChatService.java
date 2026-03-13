package com.sah.service;

import com.sah.repository.ChessLobbyChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ChessLobbyChatService {
    private String chatIdPossibleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ";
    private final short maxSize = 5;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private ChessLobbyChatRepository chessLobbyChatRepository;

    private String generateChatId() {
        StringBuilder sb = new StringBuilder();
        sb.append("CH");
        for(int i = 0; i < maxSize; i++) {
            int index = random.nextInt(chatIdPossibleChars.length());
            sb.append(chatIdPossibleChars.charAt(index));
        }
        return sb.toString();
    }

    public String assignLobbyChatId() {
        String lobbyChatId;
        do {
            lobbyChatId = generateChatId();
        } while(chessLobbyChatRepository.findByChatId(lobbyChatId) != null);
        return lobbyChatId;
    }
}
