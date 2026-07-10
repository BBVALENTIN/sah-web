package com.sah.security;

import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CurrentUserProvider {
    private final UserRepository userRepository;
    private Users cachedUser;

    public CurrentUserProvider(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Users get() {
        if(cachedUser == null) {
            CustomUserDetails principal = (CustomUserDetails)
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            cachedUser = userRepository.findById(principal.getId()).orElseThrow(() -> new IllegalStateException("User authenticated, not found in database. (adlib: what?)"));
        }

        return cachedUser;
    }
}
