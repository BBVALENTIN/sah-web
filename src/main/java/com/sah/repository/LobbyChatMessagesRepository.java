package com.sah.repository;

import com.sah.entity.ChessLobbyChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyChatMessagesRepository extends JpaRepository<ChessLobbyChatMessages, Long> {
}
