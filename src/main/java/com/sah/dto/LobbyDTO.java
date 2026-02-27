package com.sah.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.sah.enums.LobbyType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class lobbyDTO {
    public String lobbyId;
    public loggedUser loggedUsername;
    public LobbyType lobbyType;

    public lobbyDTO(String lobbyId, LobbyType lobbyType) {
        this.lobbyId = lobbyId;
        this.lobbyType = lobbyType;
    }
}
