package com.sah.game;

import com.sah.entity.ChessLobbies;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    public String playerName;
    public int color;

    public Player getPlayerWhiteFromLobby(ChessLobbies lobby) {
        return new Player(lobby.getPlayerWhite(), 1);
    }

    public Player getPlayerBlackFromLobby(ChessLobbies lobby) {
        return new Player(lobby.getPlayerBlack(), -1);
    }
}
