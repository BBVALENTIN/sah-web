package com.sah.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private boolean confirmed_Account = false;
    @Column(nullable = false)
    private int number_of_games;

    @ManyToMany
    private Set<Roles> roles;


    public Long getId() {return userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username; }

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public boolean getConfirmed_Account() { return confirmed_Account; }
    public void setConfirmed_Account(boolean Confirmed_Account) { this.confirmed_Account = Confirmed_Account; }

    public int getNumber_of_games() { return  number_of_games; }
    public void setNumber_of_games(int number_of_games) { this.number_of_games = number_of_games; }
}
