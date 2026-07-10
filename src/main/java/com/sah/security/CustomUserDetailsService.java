package com.sah.security;

import com.sah.dto.misc.loggedUser;
import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

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
            throw new UsernameNotFoundException("User doesn't exist");

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long id) throws IdentificatorNotFoundException {
        Users u = userRepo.findById(id).orElseThrow(() -> new IdentificatorNotFoundException("User doesn't exist"));

        return new CustomUserDetails(u);
    }

    public loggedUser loadInfo(Principal principal) {
        Users loggedUserAllInfo = userRepo.findByUsername(principal.getName());

        if(loggedUserAllInfo == null)
            return null;

        return new loggedUser(loggedUserAllInfo.getUserId(), loggedUserAllInfo.getUsername(), loggedUserAllInfo.getAvatar());
    }
}
