package com.sah.dto.info;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public  class UserOverviewInfoExpanded {
    private String username;
    private String description;
    private String country;
    private String avatar;

    public UserOverviewInfoExpanded(String u, String d, String c, String a){
        username = u;
        description = d;
        country = c;
        avatar = a;
    }
}
