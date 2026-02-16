package com.sah.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.sah.enums.LobbyType;

@Getter
@Setter
@AllArgsConstructor
public class lobbyDTO {
    public String lobbyId;
    public LobbyType lobbyType;
}
