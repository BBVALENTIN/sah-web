package com.sah.service.user;

import com.sah.entity.Users;
import org.springframework.security.core.Authentication;

// should be used for websocket connections
public interface ResolveAuthUser {
    public Users returnAuthenticatedUser(Authentication authentication);
}
