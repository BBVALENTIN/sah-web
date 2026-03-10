package com.sah.entity;

import com.sah.enums.FormatType;
import com.sah.enums.LobbyType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChessLobbies {
    @Id
    private String lobbyId;

    @Column(nullable = false)
    private FormatType format;
    // TO CHANGE FROM NULLABLE = TRUE TO NULLABLE = FALSE WHEN DONE
    @Column(nullable = true)
    private String playerWhite;
    @Column(nullable = true)
    private String playerBlack;
    @Column(nullable = false)
    private LobbyType lobbyType;
    @Column(nullable = true) // TO CHANGE
    private Long gameId;
    @Column(nullable = true) // TO CHANGE
    private Long chatId;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public String getLobbyId() { return lobbyId; }
    public void setLobbyId(String randomGeneratedLobbyId) { this.lobbyId = randomGeneratedLobbyId; }

    public FormatType getFormat() { return format; }
    public void setFormat(FormatType format) { this.format = format; }

    public String getPlayerWhite() { return playerWhite; }
    public void setPlayerWhite(String playerWhite) { this.playerWhite = playerWhite; }

    public String getPlayerBlack() { return playerBlack; }
    public void setPlayerBlack(String playerBlack) { this.playerBlack = playerBlack; }

    public LobbyType getLobbyType() { return lobbyType; }
    public void setLobbyType(LobbyType lobbyType) { this.lobbyType = lobbyType; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
