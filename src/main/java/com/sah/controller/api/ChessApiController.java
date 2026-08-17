package com.sah.controller.api;

import com.sah.config.WebSocketEventListener;
import com.sah.dto.chess.MinimalStateDTO;
import com.sah.dto.misc.MessageResponseDTO;
import com.sah.dto.requests.GameEndRequest;
import com.sah.dto.requests.MessageRequestDTO;
import com.sah.dto.requests.MoveRequestDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
import com.sah.enums.MessageType;
import com.sah.enums.Sides;
import com.sah.enums.WinType;
import com.sah.game.Game;
import com.sah.game.gameenums.ColorType;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.Piece;
import com.sah.repository.ChessLobbyChatRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.UserRepository;
import com.sah.security.CurrentUserProvider;
import com.sah.service.lobby.ChessLobbyChatService;
import com.sah.service.chess.GameService;
import com.sah.service.user.ResolveAuthUserImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/chess")
@AllArgsConstructor
public class ChessApiController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyRepository lobbyRepository;
    private final ChessLobbyChatService chessLobbyChatService;
    private final ChessLobbyChatRepository chessLobbyChatRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ResolveAuthUserImpl resolveCurrentUser;

//    @GetMapping("/turn")
//    public ColorType getCurrentColor()
//    {
//        return game.getCurrentColor();
//    }


//    @GetMapping("/reset")
//    public List<PieceDTO> ResetBoard()
//    {
//        game.piecesList.clear();
//        for(int i = 0; i < 8; i++)
//            for(int j = 0; j < 8; j++)
//                game.board[i][j] = null;
//        game.resetMoveNotations();
//        game.initializeBoard();
//        List<PieceDTO> dto = new ArrayList<>();
//        for(Piece p : game.getAllPieces())
//            dto.add(Game.toDTO(p, p.row, p.col));
//        return dto;
//    }

    @PostMapping("/omove")
    public ResponseEntity<?> optimisedMove(@RequestBody MoveCoords moveCoords) throws InvalidMoveException {
        try {
            Game game = gameService.getPracticeGame();
            OMoveResult res = game.makeMove(moveCoords);
            return ResponseEntity.ok(res);
        }
        catch(InvalidMoveException ex) { // maybe remove this to play a sound if a move is wrong
            throw new InvalidMoveException(ex.getErrorCodes());
        }
    }

    @MessageMapping("/chess.move")
    public void moveOnline(MoveRequestDTO request, Authentication auth) throws InvalidMoveException {
        Game currentGame = gameService.getOrCreateBoard(request.getLobbyId());
        Users currentUser = resolveCurrentUser.returnAuthenticatedUser(auth);

        Piece p = currentGame.getBoard().at(request.moveCoords.getFromRow(), request.moveCoords.getFromCol());

        if (!gameService.isValidMoveForPlayer(request.getLobbyId(), currentUser, p.color)) {
            sendError(currentUser, "You can't move your opponent's pieces");
            return;
        }

        try {
            OMoveResult result = currentGame.makeMove(request.moveCoords);

            messagingTemplate.convertAndSend("/topic/game/" + request.getLobbyId(), result);
            if (result.isCheckMate() == true) {
                ChessLobbies lobby = lobbyRepository.findByLobbyId(request.getLobbyId());
                lobby.setLobbyType(LobbyType.FINISHED);
                lobbyRepository.save(lobby);
                gameService.saveClassicGame(request.getLobbyId(), WinType.CHECKMATE, OppositeColor(currentGame.getCurrentColor()));
            }
        }
        catch (InvalidMoveException ex) {
            sendError(currentUser, ex.getErrorCodes().toString());
        }

    }

    @GetMapping("/onlineState/{lobbyId}")
    public MinimalStateDTO getOnlineState(@PathVariable String lobbyId) {
        Game currentGame = gameService.getOrCreateBoard(lobbyId);
        return new MinimalStateDTO(currentGame.getCurrentFEN(), currentGame.getFullPGN());
    }

    @MessageMapping("/chat.sendMessage/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public MessageResponseDTO sendMessage(@Payload MessageRequestDTO req,
                                          @DestinationVariable String lobbyId,
                                          Authentication auth) {

        ChessLobbyChats chat = chessLobbyChatRepository.findByLobby_LobbyId(lobbyId);
        Users user = resolveCurrentUser.returnAuthenticatedUser(auth);

        ChessLobbyChatMessages savedMessage =
                chessLobbyChatService.sendMessage(
                        chat,
                        user,
                        req.getContent()
                );

        return new MessageResponseDTO(
                savedMessage.getSender().getUsername(),
                savedMessage.getContent(),
                MessageType.CHAT
        );
    }


    @MessageMapping("/chat.addUser/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public MessageResponseDTO addUser(@DestinationVariable String lobbyId,
                                      SimpMessageHeaderAccessor headerAccessor,
                                      Authentication auth)
    {
        Users sender = resolveCurrentUser.returnAuthenticatedUser(auth);
        if(headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", sender);
            headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        }
        WebSocketEventListener.userReturned(sender.getUsername());
        return chessLobbyChatService.addUser(sender,chessLobbyChatRepository.findByLobby_LobbyId(lobbyId));
    }

    private void sendError(Users currentUser, String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);

        messagingTemplate.convertAndSendToUser(currentUser.getUsername(), "/queue/errors", errorBody);
    }


    @PostMapping("/EndGameEarly/{lobbyId}")
    public void ResignOrAbort(@PathVariable String lobbyId)
    {
        GameEndRequest request = new GameEndRequest(lobbyId, currentUserProvider.get());
        gameService.endGameEarly(request);
    }

    private Sides OppositeColor(ColorType color) {
        return color == ColorType.WHITE ? Sides.BLACK : Sides.WHITE;
    }
}

