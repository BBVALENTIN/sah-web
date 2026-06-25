package com.sah.repository;

import com.sah.entity.ChessLobbyChats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChessLobbyChatRepository extends JpaRepository<ChessLobbyChats, Long> {
    ChessLobbyChats findByChatId(Long chatId);
    ChessLobbyChats findByLobby_LobbyId(String lobbyId);
}
