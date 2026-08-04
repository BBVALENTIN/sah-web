package com.sah.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BotStartResponseDTO {
    private String gameId;
    private String currentFEN; // maybe redundant
}
