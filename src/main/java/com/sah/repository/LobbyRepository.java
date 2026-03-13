package com.sah.repository;

import com.sah.entity.ChessLobbies;
import com.sah.enums.LobbyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LobbyRepository extends JpaRepository<ChessLobbies, String> {
    ChessLobbies findByLobbyId(String lobbyId);
    List<ChessLobbies> findByLobbyType(LobbyType lobbyType);
    ChessLobbies findByChatId(String chatId);
}
