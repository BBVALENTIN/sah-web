package com.sah.service;

import com.sah.dto.info.GameOverviewInfo;
import com.sah.dto.info.UserMiscInfo;
import com.sah.dto.info.UserOverviewInfoExpanded;
import com.sah.dto.misc.MatchHistoryDTO;
import com.sah.dto.misc.ProfileInfoDTO;
import com.sah.entity.ChessGames;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
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
        Users user = userRepo.findByUsername(username);
        List<ChessLobbies> allLobbies = new ArrayList<>(lobbyRepo.findByPlayerBlackAndLobbyType(user, LobbyType.AVAILABLE));
        allLobbies.addAll(lobbyRepo.findByPlayerWhiteAndLobbyType(user, LobbyType.AVAILABLE));

        if(allLobbies.isEmpty())
           return new ArrayList<>();
        List<MatchHistoryDTO> matchHistoryDTOList = new ArrayList<>();
        for(ChessLobbies lobby : allLobbies){
            ChessGames game = gameRepo.findByLobby(lobby);

            if(game == null) {
                continue;
            }

            matchHistoryDTOList.add(new MatchHistoryDTO(getOpponent(lobby, username), lobby.getFormat(), game.getNumberOfMoves(), getOutcome(lobby, user)));
        }

        return matchHistoryDTOList;
    }

    public String getLastGamesOutcome(Principal principal) {
        Users currentUser = userRepo.findByUsername(principal.getName());
        List<ChessLobbies> allLobbies = new ArrayList<>(lobbyRepo.findTop15ByPlayerBlackAndLobbyTypeOrPlayerWhiteAndLobbyTypeOrderByCreatedAtDesc(currentUser, LobbyType.FINISHED,currentUser, LobbyType.FINISHED));
        StringBuilder resultString = new StringBuilder();
        for(ChessLobbies lobby : allLobbies)
        {
            if(getOutcome(lobby, currentUser))
                resultString.append('W');
            else
                resultString.append('L');
        }
        return resultString.toString();
    }

    private String getOpponent(ChessLobbies lobby, String username) {
        if(Objects.equals(lobby.getPlayerBlack(), username)) {
            return lobby.getPlayerWhite().getUsername();
        }
        else if(Objects.equals(lobby.getPlayerWhite(), username)) {
            return lobby.getPlayerBlack().getUsername();
        }

        throw new RuntimeException("Found a lobby with the other player being null");
    }

    private boolean getOutcome(ChessLobbies lobby, Users currentUser) { // MODIFY FOR DRAWS
        ChessGames game = gameRepo.findByLobby(lobby);

        if (game == null || game.getResult() == null) {
            return false;
        }

        return (Objects.equals(lobby.getPlayerBlack(), currentUser.getUsername()) && Objects.equals(game.getResult(), ResultType.BLACK_WIN)) || (Objects.equals(lobby.getPlayerWhite(), currentUser.getUsername()) && Objects.equals(game.getResult(), ResultType.WHITE_WIN));
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
