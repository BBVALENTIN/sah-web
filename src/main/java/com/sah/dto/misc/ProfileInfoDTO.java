package com.sah.dto.misc;

import com.sah.dto.info.GameOverviewInfo;
import com.sah.dto.info.UserOverviewInfoExpanded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProfileInfoDTO {
    // user info
    private UserOverviewInfoExpanded userInfo;
    // matches
    private GameOverviewInfo gameInfo;
}
