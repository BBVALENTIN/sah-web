package com.sah.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(nullable = false, unique = true, name="username")
    private String username;
    @Column(nullable = false, name="password")
    private String password;
    @Column(nullable = false, unique = true, name="email")
    private String email;
    @Column(name="confirmed_account")
    private boolean confirmed_account = false;
    @Column(nullable = true)
    private String description;
    @Column(nullable = true) // should be changed
    private String country; // Temporariliy - maybe will change to CountryId and make a country table
    @Column(nullable = false)
    private String avatar = "default.jpg";
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Roles> roles = new HashSet<>();
}
