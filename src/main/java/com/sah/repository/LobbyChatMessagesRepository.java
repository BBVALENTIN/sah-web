package com.sah.repository;

import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.ChessLobbyChats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyChatMessagesService extends JpaRepository<ChessLobbyChatMessages, Long> {
}
