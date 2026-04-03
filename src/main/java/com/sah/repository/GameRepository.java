package com.sah.repository;

import com.sah.entity.ChessGamesClassic;
import com.sah.entity.ChessLobbies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<ChessGamesClassic, Long> {
    ChessGamesClassic findByLobby(ChessLobbies lobby);
}
