package com.sah.repository;

import com.sah.entity.Users;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    @Override
    Optional<Users> findById(Long aLong);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
