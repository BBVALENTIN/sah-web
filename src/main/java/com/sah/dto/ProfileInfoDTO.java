package com.sah.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileInfoDTO {
    // user info
    private String username;
    private String description;
    private String country;

    // matches
    private int gamesPlayed;
    private List<MatchHistoryDTO> matchHistoryDTOList;
}
