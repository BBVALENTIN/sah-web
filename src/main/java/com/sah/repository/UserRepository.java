package com.sah.repository;

import com.sah.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository  extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
