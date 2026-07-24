package com.sah.controller.api;

import com.sah.dto.misc.LobbyDTO;
import com.sah.entity.ChessLobbies;
import com.sah.enums.LobbyType;
import com.sah.service.lobby.ChessLobbyService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

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
    public String joinLobby(@PathVariable String lobbyId) {
        ChessLobbies lobby = chessLobbyService.getLobbyFromId(lobbyId);
        if(chessLobbyService.isLobbyFull(lobby))
        {
            return "lobbyFull";
        }
        chessLobbyService.joinLobby(lobbyId);
        return "play="+lobbyId;
    }

    @GetMapping("/getLobbyDTO")
    public LobbyDTO getLobbyDTO(@RequestParam String lobbyId, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        return chessLobbyService.getLobbyDTO(lobbyId);
    }

    @GetMapping("/createQuick")
    public String createQuickLobby() {
        return chessLobbyService.createLobby();
    }

    @GetMapping("/create")
    public String createLobby() {
        return chessLobbyService.createLobby();
    }
}
