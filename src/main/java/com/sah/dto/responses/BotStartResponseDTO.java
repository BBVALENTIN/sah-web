package com.sah.dto.responses;

import com.sah.dto.chess.PieceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotStartResponseDTO {
    private String gameId;
    private List<PieceDTO> pieces;
}
