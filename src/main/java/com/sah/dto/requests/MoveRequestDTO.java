package com.sah.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveRequestDTO {
    public int fromRow;
    public int fromCol;
    public int toRow;
    public int toCol;
    public String player;
    public String lobbyId;
}
