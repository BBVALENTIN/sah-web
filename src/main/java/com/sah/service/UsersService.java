package com.sah.service;

import com.sah.dto.RegisterRequest;
import com.sah.entity.Roles;
import com.sah.entity.Users;
import com.sah.enums.RoleType;
import com.sah.repository.RoleRepository;
import com.sah.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.ArrayList;

@Service
@Profile("prod")
public class UsersService {

    private final UserRepository userRepo;
    private final RoleRepository rolesRepo;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UserRepository userRepo, RoleRepository rolesRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if(userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is taken");
        } else if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Roles userRole = rolesRepo.findByName(RoleType.ROLE_USER).orElseThrow(() -> new RuntimeException("ROLE_user nu exista"));

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
    }
}
