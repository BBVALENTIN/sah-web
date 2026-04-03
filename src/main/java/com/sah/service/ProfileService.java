package com.sah.service;

import com.sah.dto.MatchHistoryDTO;
import com.sah.entity.ChessGamesClassic;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.ResultType;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.RoleRepository;
import com.sah.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProfileService {
    private UserRepository userRepo;
    private RoleRepository rolesRepo;
    private GameRepository gameRepo;
    private LobbyRepository lobbyRepo;

    public ProfileService(UserRepository userRepo, RoleRepository rolesRepo, GameRepository gameRepo,  LobbyRepository lobbyRepo) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.gameRepo = gameRepo;
        this.lobbyRepo = lobbyRepo;
    }

    public String loadProfileInfo() {
        return "toAdd";
    }

    public List<MatchHistoryDTO> loadProfileGames(String username) {
        List<ChessLobbies> allLobbies = new ArrayList<>(lobbyRepo.findByPlayerBlack(username));
        allLobbies.addAll(lobbyRepo.findByPlayerWhite(username));

        if(allLobbies == null || allLobbies.isEmpty())
           return null;
        List<MatchHistoryDTO> matchHistoryDTOList = new ArrayList<>();
        for(ChessLobbies lobby : allLobbies){
            ChessGamesClassic game = gameRepo.findByLobby(lobby);
            matchHistoryDTOList.add(new MatchHistoryDTO(getOpponent(lobby, username), lobby.getFormat(), game.getNumberOfMoves(), getOutcome(lobby, username)));
        }

        return matchHistoryDTOList;
    }

    private String getOpponent(ChessLobbies lobby, String username) {
        if(Objects.equals(lobby.getPlayerBlack(), username)) {
            return lobby.getPlayerWhite();
        }
        else if(Objects.equals(lobby.getPlayerWhite(), username)) {
            return lobby.getPlayerBlack();
        }

        throw new RuntimeException("Found a lobby with the other player being null");
    }

    private boolean getOutcome(ChessLobbies lobby, String username) { // MODIFY FOR DRAWS
        ChessGamesClassic game = gameRepo.findByLobby(lobby);
        if(Objects.equals(lobby.getPlayerBlack(), username) && Objects.equals(game.getResult(), ResultType.BLACK_WIN)) {
            return true;
        }
        return false;
    }
}
