package com.sah.dto.chess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CastlingInfoDTO {
    public boolean possibleCastleBlackShort = true, possibleCastleWhiteShort = true, possibleCastleBlackLong = true, possibleCastleWhiteLong = true;
}
