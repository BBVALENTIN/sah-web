package com.sah.controller.api;

import com.sah.dto.misc.CreateLobbyRequest;
import com.sah.dto.misc.LobbyDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
import com.sah.service.ChessLobbyService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class LobbyApiController {

    private final ChessLobbyService chessLobbyService;

    public LobbyApiController(ChessLobbyService cls)
    {
        chessLobbyService = cls;
    }

    @GetMapping("/{desiredLobby}")
    public List<LobbyDTO> getLobbies(@PathVariable LobbyType desiredLobby) {
        return chessLobbyService.getAllDesiredLobbies(desiredLobby);
    }

    @PostMapping("/{lobbyId}")
    public String joinLobby(@PathVariable String lobbyId, Principal principal) {
        ChessLobbies lobby = chessLobbyService.getLobbyFromId(lobbyId);
        if(chessLobbyService.isLobbyFull(lobby))
        {
            return "lobbyFull";
        }
        chessLobbyService.joinLobby(lobbyId, principal.getName());
        return "play="+lobbyId;
    }

    @GetMapping("/getLobbyDTO")
    public LobbyDTO getLobbyDTO(@RequestParam String lobbyId, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        return chessLobbyService.getLobbyDTO(lobbyId);
    }

    @GetMapping("/createQuick")
    public String createQuickLobby(Principal principal) {
        return chessLobbyService.createLobby(principal.getName());
    }

    @GetMapping("/create")
    public String createLobby(Principal principal) {
        return chessLobbyService.createLobby(principal.getName());
    }
}
