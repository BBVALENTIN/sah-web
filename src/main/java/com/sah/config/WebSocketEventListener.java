package com.sah.config;

import com.sah.dto.misc.LobbyDTO;
import com.sah.dto.misc.MessageResponseDTO;
import com.sah.entity.ChessLobbies;
import com.sah.enums.LobbyType;
import com.sah.enums.MessageType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.Game;
import com.sah.repository.LobbyRepository;
import com.sah.service.lobby.ChessLobbyService;
import com.sah.service.chess.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChessLobbyService lobbyService;
    private final GameService gameService;
    private final LobbyRepository lobbyRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static Map<String, String> pendingReconnections = new ConcurrentHashMap<>();

    public static void userReturned(String username) {
        if (username != null) {
            log.info("The user {} is back on the page.", username);
            pendingReconnections.remove(username);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String lobbyId = (String) headerAccessor.getSessionAttributes().get("lobbyId");


        if(lobbyId != null && username != null)
        {
            log.info("User disconnected: {}", username);
            pendingReconnections.put(username, lobbyId);
            var chatMessage = MessageResponseDTO.builder().type(MessageType.LEAVE).sender(username).build();
            messageTemplate.convertAndSend("/topic/public", chatMessage);

            scheduler.schedule(() -> {
                verifyReconnect(lobbyId, username, sessionId);
            }, 10, TimeUnit.SECONDS);
        }
        else {
            lobbyService.handlePlayerDisconnect(sessionId);
        }
    }

    private void verifyReconnect(String lobbyId, String username, String sessionId) {
        ChessLobbies lobby = lobbyRepository.findByLobbyId(lobbyId);
        if(lobby == null) return;

        if(username != null && !pendingReconnections.containsKey(username)) {
            log.info("The user {} reconnected, lobby Id: {}.", username, lobby.getLobbyId());
            return;
        }

        if (username != null) {
            pendingReconnections.remove(username);
        }

        Game game = gameService.getBoard(lobby.getLobbyId());
        Sides winner = Sides.NONE;

        if(game != null && game.getFullMove() > 2) {
            if (username != null && lobby.getPlayerWhite() != null
                    && username.equals(lobby.getPlayerWhite().getUsername())) {
                winner = Sides.BLACK;
            } else {
                winner = Sides.WHITE;
            }

            lobby.setLobbyType(LobbyType.FINISHED);
            lobbyRepository.save(lobby);
            gameService.saveClassicGame(lobbyId, WinType.ABANDONMENT, winner);
            LobbyDTO finishedLobby = lobbyService.convertLobbyDTO(lobby);
            messageTemplate.convertAndSend("/topic/lobby/" + lobby.getLobbyId(), finishedLobby);
            messageTemplate.convertAndSend("/topic/global-lobbies", finishedLobby);

            lobbyService.handlePlayerDisconnect(sessionId);
            return;
        }

        if (username != null && lobby.getPlayerWhite() != null
                && username.equals(lobby.getPlayerWhite().getUsername())) {
            lobby.setPlayerWhite(null);
        } else if (username != null && lobby.getPlayerBlack() != null
                && username.equals(lobby.getPlayerBlack().getUsername())) {
            lobby.setPlayerBlack(null);
        }

        if(lobby.getPlayerBlack() == null && lobby.getPlayerWhite() == null){
            lobbyRepository.delete(lobby);

            LobbyDTO removedLobby = lobbyService.convertLobbyDTO(lobby);
            removedLobby.setLobbyType(LobbyType.ABORTED);
            messageTemplate.convertAndSend("/topic/global-lobbies", removedLobby);
        } else {
            lobby.setLobbyType(LobbyType.AVAILABLE);
            lobbyRepository.save(lobby);

            LobbyDTO updatedLobby = lobbyService.convertLobbyDTO(lobby);
            messageTemplate.convertAndSend("/topic/lobby/" + lobby.getLobbyId(), updatedLobby);
            messageTemplate.convertAndSend("/topic/global-lobbies", updatedLobby);
        }

        lobbyService.handlePlayerDisconnect(sessionId);
    }
}
