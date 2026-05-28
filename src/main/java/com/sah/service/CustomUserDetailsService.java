package com.sah.service;

import com.sah.dto.loggedUser;
import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Users user = userRepo.findByUsername(username);

        if(user == null)
            throw new UsernameNotFoundException("Nu exista acest utilizator");

        return new User(user.getUsername(), user.getPassword(), user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).toList());
    }


    public loggedUser loadInfo(Principal principal) {
        Users loggedUserAllInfo = userRepo.findByUsername(principal.getName());

        return new loggedUser(loggedUserAllInfo.getUserId(), loggedUserAllInfo.getUsername(), loggedUserAllInfo.getAvatar());
    }
}
