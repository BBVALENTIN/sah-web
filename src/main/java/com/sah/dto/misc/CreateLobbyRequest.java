package com.sah.dto.misc;

import com.sah.enums.LobbyType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLobbyRequest {
    private LobbyType lobbyType;
    private String username;
}
