package com.sah.repository;

import com.sah.entity.Roles;
import com.sah.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sah.entity.Roles;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(RoleType name);
}
