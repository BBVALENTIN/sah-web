package com.sah.dto.misc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.sah.enums.LobbyType;

@Getter
@Setter
@NoArgsConstructor
public class LobbyDTO {
    public String lobbyId;
    public LobbyType lobbyType;
    public String playerWhite;
    public String playerBlack;

    public LobbyDTO(String lobbyId, LobbyType lobbyType) {
        this.lobbyId = lobbyId;
        this.lobbyType = lobbyType;
    }

    public LobbyDTO(String lobbyId, LobbyType lobbyType, String playerWhite, String playerBlack) {
        this.lobbyId = lobbyId;
        this.lobbyType = lobbyType;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
    }
}
