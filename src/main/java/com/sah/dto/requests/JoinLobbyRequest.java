package com.sah.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinLobbyRequest {
    public String lobbyId;
    public String username;
}
