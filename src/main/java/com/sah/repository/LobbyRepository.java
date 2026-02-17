package com.sah.repository;

import com.sah.entity.Chess_Lobby;
import com.sah.enums.LobbyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LobbyRepository extends JpaRepository<Chess_Lobby, String> {
    Chess_Lobby findByLobbyId(String lobbyId);
    List<Chess_Lobby> findByLobbyType(LobbyType lobbyType);
}
