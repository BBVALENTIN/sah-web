package com.sah.entity;

import jakarta.persistence.*;

@Entity
public class ChessGamesClassic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ClassicGameId;

    @Column(nullable = false)
    private int userWhiteId;
    @Column(nullable = false)
    private int userBlackId;
    @Column(nullable = false)
    private String result;
    @Column(nullable = false)
    private int number_of_moves = 0;
    @Column(nullable = false)
    private String PGN;
    @Column(nullable = false)
    private String lobbyId;

    public Long getClassicGameId() { return ClassicGameId; }

    public int getUserWhiteId() { return userWhiteId; }
    public void setUserWhiteId(int userWhiteId) { this.userWhiteId = userWhiteId; }

    public int getUserBlackId() { return userBlackId; }
    public void setUserBlackId(int userBlackId) { this.userBlackId = userBlackId; }

    public String getResult() { return result; }
    public void setResult() { this.result = result; }

    public String getPGN() { return PGN; }
    public void setPGN(String PGN) { this.PGN = PGN; }

    public String getLobbyId() {return this.lobbyId; }
    public void setLobbyId(String lobbyId) { this.lobbyId = lobbyId; }

}
