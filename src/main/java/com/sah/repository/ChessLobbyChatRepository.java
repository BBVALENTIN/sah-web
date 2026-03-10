package com.sah.repository;

import com.sah.entity.ChessLobbyChats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChessLobbyChatRepository extends JpaRepository<ChessLobbyChats, String> {
    ChessLobbyChats findByChatId(String lobbyChatId);
}
