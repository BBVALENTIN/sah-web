package com.sah.entity;

import com.sah.enums.LobbyType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

public class Chess_lobby {
    @Id
    private String lobby_Id;

    @Column(nullable = false, name = "format")
    private String format;
    @Column(nullable = false)
    private String player_white;
    @Column(nullable = false)
    private String player_black;
    @Column(nullable = false)
    private LobbyType lobbyType;

    public String getLobby_Id() { return lobby_Id; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getPlayer_white() { return player_white; }
    public void setPlayer_white(String player_white) { this.player_white = player_white; }

    public String getPlayer_black() { return player_black; }
    public void setPlayer_black(String player_black) { this.player_black = player_black; }

    public LobbyType getLobbyType() { return lobbyType; }
    public void setLobbyType(LobbyType lobbyType) { this.lobbyType = lobbyType; }
}
