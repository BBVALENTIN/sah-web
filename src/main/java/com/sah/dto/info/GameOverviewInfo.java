package com.sah.dto.info;

import com.sah.dto.misc.MatchHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class GameOverviewInfo {
    private int gamesPlayed;
    private List<MatchHistoryDTO> matchHistoryDTOList;
}
