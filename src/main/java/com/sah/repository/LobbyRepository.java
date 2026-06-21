package com.sah.repository;

import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LobbyRepository extends JpaRepository<ChessLobbies, String> {
    ChessLobbies findByLobbyId(String lobbyId);
    List<ChessLobbies> findByLobbyType(LobbyType lobbyType);
    List<ChessLobbies> findByPlayerBlackAndLobbyType(Users user, LobbyType lt);
    List<ChessLobbies> findByPlayerWhiteAndLobbyType(Users user, LobbyType lt);
    List<ChessLobbies> findTop15ByPlayerBlackAndLobbyTypeOrPlayerWhiteAndLobbyTypeOrderByCreatedAtDesc(Users playerBlack, LobbyType lt1, Users playerWhite, LobbyType lt2);
}
