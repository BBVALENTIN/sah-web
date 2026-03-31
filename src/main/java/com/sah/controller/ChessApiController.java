package com.sah.controller;

import com.sah.dto.*;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.enums.MessageType;
import com.sah.enums.Sides;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.piese.Piese;
import com.sah.repository.GameRepository;
import com.sah.repository.LobbyRepository;
import com.sah.service.ChessLobbyChatService;
import com.sah.service.ChessLobbyService;
import com.sah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/chess")
public class ChessApiController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChessBoard chessBoard;
    private final ChessLobbyService  chessLobbyService;
    private final ChessLobbyChatService chessLobbyChatService;
    private final GameRepository gameRepository;
    private final LobbyRepository lobbyRepository;

    @Autowired
    public ChessApiController(GameService gameService, SimpMessagingTemplate messagingTemplate, ChessBoard chessBoard, ChessLobbyChatService chessLobbyChatService, GameRepository gameRepository, ChessLobbyService chessLobbyService, LobbyRepository lobbyRepository) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
        this.chessBoard = chessBoard;
        this.chessLobbyChatService = chessLobbyChatService;
        this.gameRepository = gameRepository;
        this.chessLobbyService = chessLobbyService;
        this.lobbyRepository = lobbyRepository;
    }


    // For single player
    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestParam int fromRow,
                           @RequestParam int fromCol,
                           @RequestParam int toRow,
                           @RequestParam int toCol
    ) {
        MoveResultDTO result =  chessBoard.faMiscare(fromRow, fromCol, toRow, toCol);

        if(result.getErrorCodes() != null) {
            return ResponseEntity.badRequest().body(result.getErrorCodes());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/state")
    public List<PiesaDTO> getState() {
        return chessBoard.getAllPiecesDTO();
    }

    @GetMapping("/turn")
    public ColorType getCuloareCurenta()
    {
        return chessBoard.getCuloareCurenta();
    }


//    @GetMapping("/reset")
//    public List<PiesaDTO> ResetBoard()
//    {
//        chessBoard.pieseList.clear();
//        for(int i = 0; i < 8; i++)
//            for(int j = 0; j < 8; j++)
//                chessBoard.board[i][j] = null;
//        chessBoard.allFormattedMoves = "";
//        chessBoard.initializeBoard();
//        List<PiesaDTO> dto = new ArrayList<>();
//        for(Piese p : chessBoard.getAllPieces())
//            dto.add(ChessBoard.toDTO(p));
//        return dto;
//    }

    @MessageMapping("/chess.move")
    public void moveOnline(MoveRequestDTO request) {
        ChessBoard lobbyBoard = gameService.getOrCreateBoard(request.getLobbyId());
        Piese piesa = lobbyBoard.board[request.getFromRow()][request.getFromCol()];

        if (piesa == null) {
            sendError(request.getPlayer(), "Nu există nicio piesă acolo!");
            return;
        }

        if (!gameService.isValidMoveForPlayer(request.getLobbyId(), request.getPlayer(), piesa.color)) {
            sendError(request.getPlayer(), "Nu poți muta piesele adversarului!");
            return;
        }
        MoveResultDTO result = lobbyBoard.faMiscare(request.getFromRow(), request.getFromCol(), request.getToRow(), request.getToCol());

        if(result.getErrorCodes() == null) {
            messagingTemplate.convertAndSend("/topic/game/" + request.getLobbyId(), result);
            if(result.isCheckmate() == true) {
                gameService.saveClassicGame(request.getLobbyId());
            }
        }
        else {
            messagingTemplate.convertAndSendToUser(request.getPlayer(), "/queue/errors", result.getErrorCodes());
        }
    }

    @GetMapping("/onlineState/{lobbyId}")
    public MinimalStateDTO getOnlineState(@PathVariable String lobbyId) {
        ChessBoard lobbyBoard = gameService.getOrCreateBoard(lobbyId);
        MinimalStateDTO minimalStateDTO = new MinimalStateDTO(lobbyBoard.getAllPiecesDTO(), lobbyBoard.culoareCurenta, lobbyBoard.getAllPGN());
        return minimalStateDTO;
    }

    @MessageMapping("/chat.sendMessage/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO,
                                      @DestinationVariable String lobbyId,
                                      Principal principal) {

        ChessLobbyChatMessages savedMessage =
                chessLobbyChatService.sendMessage(
                        lobbyId,
                        principal.getName(),
                        chatMessageDTO.getContent()
                );

        return new ChatMessageDTO(
                savedMessage.getSenderName(),
                savedMessage.getContent(),
                MessageType.CHAT
        );
    }


    @MessageMapping("/chat.addUser/{lobbyId}")
    @SendTo("/topic/chat/{lobbyId}")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, @DestinationVariable String lobbyId) {
        return chessLobbyChatService.addUser(chatMessageDTO.getSender(), lobbyId);
    }

    private void sendError(String username, String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);

        messagingTemplate.convertAndSendToUser(username, "/queue/errors", errorBody);
    }

    @PostMapping("/resign")
    public void Resignation(@RequestBody ResignRequestDTO dto) {
        lobbyValidation(dto);

        ChessBoard board = gameService.getBoard(dto.lobbyId);
        board.setResignation(true);
        if(Objects.equals(dto.color, "ALB"))
            board.setWinner(Sides.WHITE);
        else
            board.setWinner(Sides.BLACK);
        gameService.saveClassicGame(dto.lobbyId);
    }

    @PostMapping("/abort")
    public void Abort(@RequestBody ResignRequestDTO dto) {
        lobbyValidation(dto);
        chessLobbyService.abortGame(dto.lobbyId);
    }

    private void lobbyValidation(@RequestBody ResignRequestDTO dto) {
        if(dto.lobbyId == null)
        {
            throw new RuntimeException("Lobby id null!");
        }
        if(gameService.getBoard(dto.lobbyId) == null) {
            throw new RuntimeException("Can't find the lobby!");
        }
        if(gameRepository.findByLobbyId(dto.lobbyId) != null)
        {
            throw new RuntimeException("Lobby already exists! I don't know how you got here game's over my boy.");
        }
    }
}

