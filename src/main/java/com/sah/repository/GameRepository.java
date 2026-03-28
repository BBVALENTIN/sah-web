package com.sah.repository;

import com.sah.entity.ChessGamesClassic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<ChessGamesClassic, Long> {
    ChessGamesClassic findByLobbyId(String lobbyId);
}
