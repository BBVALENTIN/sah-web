package com.sah.repository;

import com.sah.entity.BotGames;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotGameRepository extends JpaRepository<BotGames, Long> {
    BotGames findByGameId(String gameId);
}
