package com.sah.dto.chess;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MinimalStateDTO {
    public String currentFEN;
    public String currentPGN;
}
