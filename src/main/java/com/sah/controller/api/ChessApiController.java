package com.sah.controller.api;

import com.sah.config.WebSocketEventListener;
import com.sah.dto.chess.MinimalStateDTO;
import com.sah.dto.chess.PieceDTO;
import com.sah.dto.misc.ChatMessageDTO;
import com.sah.dto.requests.GameEndRequest;
import com.sah.dto.requests.MoveRequestDTO;
import com.sah.dto.responses.MoveResultDTO;
import com.sah.entity.ChessLobbies;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.entity.ChessLobbyChats;
import com.sah.entity.Users;
import com.sah.enums.LobbyType;
import com.sah.enums.MessageType;
import com.sah.enums.WinType;
import com.sah.game.ChessBoard;
import com.sah.game.gameenums.ColorType;
import com.sah.game.exceptions.InvalidMoveException;
import com.sah.game.dtos.MoveCoords;
import com.sah.game.dtos.OMoveResult;
import com.sah.game.pieces.Pieces;
import com.sah.repository.ChessLobbyChatRepository;
import com.sah.repository.LobbyRepository;
import com.sah.repository.UserRepository;
import com.sah.security.CurrentUserProvider;
import com.sah.security.CustomUserDetails;
import com.sah.service.lobby.ChessLobbyChatService;
import com.sah.service.chess.GameService;
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

import java.security.Principal;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/chess")
@AllArgsConstructor
public class ChessApiController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChessBoard chessBoard;
    private final LobbyRepository lobbyRepository;
    private final ChessLobbyChatService chessLobbyChatService;
    private final ChessLobbyChatRepository chessLobbyChatRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/turn")
    public ColorType getCurrentColor()
    {
        return chessBoard.getCurrentColor();
    }


    @GetMapping("/reset")
    public List<PieceDTO> ResetBoard()
    {
        chessBoard.piecesList.clear();
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                chessBoard.board[i][j] = null;
        chessBoard.resetMoveNotations();
        chessBoard.initializeBoard();
        List<PieceDTO> dto = new ArrayList<>();
        for(Pieces p : chessBoard.getAllPieces())
            dto.add(ChessBoard.toDTO(p, p.row, p.col));
        return dto;
    }

    @PostMapping("/omove")
    public ResponseEntity<?> optimisedMove(@RequestBody MoveCoords moveCoords) throws InvalidMoveException {
        try {
            OMoveResult res = chessBoard.makeOptimisedMove(moveCoords);
            return ResponseEntity.ok(res);
        }
        catch(InvalidMoveException ex) { // maybe remove this to play a sound if a move is wrong
            throw new InvalidMoveException(ex.getErrorCodes());
        }
    }

    @MessageMapping("/chess.move")
    public void moveOnline(MoveRequestDTO request, Authentication auth) throws InvalidMoveException {
        ChessBoard lobbyBoard = gameService.getOrCreateBoard(request.getLobbyId());
        Pieces piece = lobbyBoard.board[request.moveCoords.getFromRow()][request.getMoveCoords().getFromCol()];
        Users currentUser = resolveCurrentUser(auth);
        if (piece == null) {
            sendError(currentUser, "No piece detected");
            return;
        }

        if (!gameService.isValidMoveForPlayer(request.getLobbyId(), currentUser, piece.color)) {
            sendError(currentUser, "You can't move your opponent's pieces");
            return;
        }

        try {
            OMoveResult result = lobbyBoard.makeOptimisedMove(request.moveCoords);

            messagingTemplate.convertAndSend("/topic/game/" + request.getLobbyId(), result);
            if (result.isCheckMate() == true) {
                ChessLobbies lobby = lobbyRepository.findByLobbyId(request.getLobbyId());
                lobby.setLobbyType(LobbyType.FINISHED);
                lobbyRepository.save(lobby);
                gameService.saveClassicGame(request.getLobbyId(), WinType.CHECKMATE);
            }
        }
        catch (InvalidMoveException ex) {
            sendError(currentUser, ex.getErrorCodes().toString());
        }

    }


    @GetMapping("/onlineState/{lobbyId}")
    public MinimalStateDTO getOnlineState(@PathVariable String lobbyId) {
        ChessBoard lobbyBoard = gameService.getOrCreateBoard(lobbyId);
        return new MinimalStateDTO(lobbyBoard.getAllPiecesDTO(), lobbyBoard.currentColor, lobbyBoard.getAllPGN());
    }

    @MessageMapping("/chat.sendMessage/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO,
                                      @DestinationVariable String lobbyId) {

        ChessLobbyChats chat = chessLobbyChatRepository.findByLobby_LobbyId(lobbyId);
        Users user = currentUserProvider.get();

        ChessLobbyChatMessages savedMessage =
                chessLobbyChatService.sendMessage(
                        chat,
                        user,
                        chatMessageDTO.getContent()
                );

        return new ChatMessageDTO(
                savedMessage.getSender().getUsername(),
                savedMessage.getContent(),
                MessageType.CHAT
        );
    }


    @MessageMapping("/chat.addUser/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, @DestinationVariable String lobbyId, SimpMessageHeaderAccessor headerAccessor) {
        Users sender = userRepository.findByUsername(chatMessageDTO.getSender());
        if(headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessageDTO.getSender());
            headerAccessor.getSessionAttributes().put("lobbyId", lobbyId);
        }
        WebSocketEventListener.userReturned(chatMessageDTO.getSender());
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

    private Users resolveCurrentUser(Authentication auth)
    {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("User authenticated but not found in the database"));
    }
}

