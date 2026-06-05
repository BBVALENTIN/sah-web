package com.sah.service;

import com.sah.entity.Users;
import com.sah.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final UserRepository userRepository;

    public SettingsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void changeDescription(String username, String content) {
        Users user = userRepository.findByUsername(username);
        if(user == null) return;

        user.setDescription(content);
        userRepository.save(user);
    }

    public void changeCountry(String username, String content) {
        Users user = userRepository.findByUsername(username);
        if(user == null) return;

        user.setCountry(content);
        userRepository.save(user);
    }
}
