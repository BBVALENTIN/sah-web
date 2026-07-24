package com.sah.service.user;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.sah.dto.requests.RegisterRequestDTO;
import com.sah.entity.Roles;
import com.sah.entity.Users;
import com.sah.enums.RoleType;
import com.sah.repository.RoleRepository;
import com.sah.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class UsersService {

    private final UserRepository userRepo;
    private final RoleRepository rolesRepo;
    private final PasswordEncoder passwordEncoder;
    /*
    * This requires you to have some kind of database regarding IP allocation of countries, for instance, I use the GeoLite2 as mentioned in the README.md
    * */
    private DatabaseReader reader = new DatabaseReader.Builder(new File("geodb-lite/GeoLite2-Country.mmdb")).build();
    private final String usernameRegex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{3,20}$";

    public UsersService(UserRepository userRepo, RoleRepository rolesRepo, PasswordEncoder passwordEncoder) throws IOException {
        this.userRepo = userRepo;
        this.rolesRepo = rolesRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterRequestDTO request) {
        if(userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is taken");
        } else if (userRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        if(!request.getUsername().matches(usernameRegex)) {
            throw new RuntimeException("Username is invalid, it should contain at least one letter and consist of at least 3 characters.");
        }

        Roles userRole = rolesRepo.findByName(RoleType.ROLE_USER).orElseThrow(() -> new RuntimeException("ROLE_user doesn't exist"));

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCountry(getCountryFromIp(request.getIp()));
        user.getRoles().add(userRole);

        userRepo.save(user);
    }

    private String getCountryFromIp(String ip)  {
        try {
            if(ip.equals("127.0.0.1") || ip.contains("0:0:0"))
                return "ro";
            CountryResponse response = reader.country(InetAddress.getByName(ip));
            return response.country().isoCode().toLowerCase();
        } catch (Exception e) {
            return "cn";
        }
    }
}
