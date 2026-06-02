package com.sah.dto.misc;

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
    private String avatar;
    // matches
    private int gamesPlayed;
    private List<MatchHistoryDTO> matchHistoryDTOList;
}
