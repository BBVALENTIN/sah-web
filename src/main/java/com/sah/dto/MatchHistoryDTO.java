package com.sah.dto;

import com.sah.enums.FormatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchHistoryDTO {
    public String opponent;
    public FormatType format;
    public int numberOfMoves;
    public boolean outcome;
}
