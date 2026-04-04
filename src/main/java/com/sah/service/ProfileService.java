package com.sah.service;

import com.sah.dto.MatchHistoryDTO;
import com.sah.dto.ProfileInfoDTO;
import com.sah.entity.ChessGames;
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

    public ProfileInfoDTO loadProfileInfo(String username) {
       ProfileInfoDTO profileInfoDTO = new ProfileInfoDTO();
       List<MatchHistoryDTO> matchHistoryDTOList = new ArrayList<>(loadProfileGames(username));

       Users user = userRepo.findByUsername(username);

       if(user != null) {
           profileInfoDTO.setUsername(username);
           profileInfoDTO.setDescription(user.getDescription());
           profileInfoDTO.setCountry(user.getCountry());
           profileInfoDTO.setMatchHistoryDTOList(matchHistoryDTOList);
           profileInfoDTO.setGamesPlayed(matchHistoryDTOList.size());
       }
       return profileInfoDTO;
    }

    private List<MatchHistoryDTO> loadProfileGames(String username) {
        List<ChessLobbies> allLobbies = new ArrayList<>(lobbyRepo.findByPlayerBlack(username));
        allLobbies.addAll(lobbyRepo.findByPlayerWhite(username));

        if(allLobbies.isEmpty())
           return new ArrayList<>();
        List<MatchHistoryDTO> matchHistoryDTOList = new ArrayList<>();
        for(ChessLobbies lobby : allLobbies){
            ChessGames game = gameRepo.findByLobby(lobby);

            if(game == null) {
                continue;
            }

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
        ChessGames game = gameRepo.findByLobby(lobby);
        if(Objects.equals(lobby.getPlayerBlack(), username) && Objects.equals(game.getResult(), ResultType.BLACK_WIN)) {
            return true;
        }
        return false;
    }
}
