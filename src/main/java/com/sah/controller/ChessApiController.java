package com.sah.controller;

import com.sah.dto.*;
import com.sah.entity.ChessLobbyChatMessages;
import com.sah.enums.MessageType;
import com.sah.game.ChessBoard;
import com.sah.game.GameEnums.ColorType;
import com.sah.game.piese.Piese;
import com.sah.service.ChessLobbyChatService;
import com.sah.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.awt.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chess")
public class ChessApiController {
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChessBoard chessBoard;
    private final ChessLobbyChatService chessLobbyChatService;

    @Autowired
    public ChessApiController(GameService gameService, SimpMessagingTemplate messagingTemplate, ChessBoard chessBoard, ChessLobbyChatService chessLobbyChatService) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
        this.chessBoard = chessBoard;
        this.chessLobbyChatService = chessLobbyChatService;
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
        }
        else {
            messagingTemplate.convertAndSendToUser(request.getPlayer(), "/queue/errors", result.getErrorCodes());
        }
    }

    @GetMapping("/onlineState/{lobbyId}")
    public minimalStateDTO getOnlineState(@PathVariable String lobbyId) {
        ChessBoard lobbyBoard = gameService.getOrCreateBoard(lobbyId);
        minimalStateDTO minimalStateDTO = new minimalStateDTO(lobbyBoard.getAllPiecesDTO(), lobbyBoard.culoareCurenta, lobbyBoard.getPGN());
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

    @PostMapping("/saveGame")
    public GameDTO saveGame(@RequestBody GameDTO gameDTO) {

    }
}

