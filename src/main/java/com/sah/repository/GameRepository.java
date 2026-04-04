package com.sah.repository;

import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<ChessGames, Long> {
    ChessGames findByLobby(ChessLobbies lobby);
}
