package com.sah.service.user;

import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import com.sah.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResolveAuthUserImpl implements ResolveAuthUser {

    private final UserRepository userRepository;

    public Users returnAuthenticatedUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("User authenticated but not found in the database"));    }
}
