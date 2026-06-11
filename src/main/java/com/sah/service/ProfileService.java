package com.sah.service;

import com.sah.dto.info.GameOverviewInfo;
import com.sah.dto.info.UserMiscInfo;
import com.sah.dto.info.UserOverviewInfoExpanded;
import com.sah.dto.misc.MatchHistoryDTO;
import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.ResultType;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.RoleRepository;
import com.sah.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
       List<MatchHistoryDTO> matchHistoryDTOList = new ArrayList<>(loadProfileGames(username));

       Users user = userRepo.findByUsername(username);

       if(user != null) {

           return new ProfileInfoDTO(
                   new UserOverviewInfoExpanded(username, user.getDescription(), user.getCountry().toLowerCase() ,user.getAvatar()),
                   new GameOverviewInfo(matchHistoryDTOList.size(), matchHistoryDTOList)
           );
       }
       return null;
    }

    public UserMiscInfo returnMiscInfo(String username) {
        Users user = userRepo.findByUsername(username);
        return new UserMiscInfo(username, user.getAvatar());
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

    public String getLastGamesOutcome(Principal principal) {
        String currentUsername = principal.getName();
        List<ChessLobbies> allLobbies = new ArrayList<>(lobbyRepo.findTop15ByPlayerBlackOrPlayerWhiteOrderByCreatedAtDesc(currentUsername, currentUsername));
        StringBuilder resultString = new StringBuilder();
        for(ChessLobbies lobby : allLobbies)
        {
            if(getOutcome(lobby, currentUsername))
                resultString.append('W');
            else
                resultString.append('L');
        }

        return resultString.toString();
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

        if (game == null || game.getResult() == null) {
            return false;
        }

        if(Objects.equals(lobby.getPlayerBlack(), username) && Objects.equals(game.getResult(), ResultType.BLACK_WIN)) {
            return true;
        }
        return false;
    }

    public String changeDescription(String description, Principal principal) {
        if(description.length() >= 50)
            return "Your description is too long";

        Users currentUser = userRepo.findByUsername(principal.getName());
        currentUser.setDescription(description);
        userRepo.save(currentUser);
        return "Description saved successfully";
    }

    public String changeCountry(String countryCode, Principal principal) // expects ISO code
    {
        Users currentUser = userRepo.findByUsername(principal.getName());
        currentUser.setCountry(countryCode.toLowerCase());
        return "Country changed successful";
    }
}
