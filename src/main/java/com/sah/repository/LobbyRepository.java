package com.sah.repository;

import com.sah.entity.Chess_lobby;
import com.sah.enums.LobbyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LobbyRepository extends JpaRepository<Chess_lobby, Long> {
    Chess_lobby findByLobby_Id(String lobbyId);
    List<Chess_lobby> findByTip(LobbyType lobbyType);
}
