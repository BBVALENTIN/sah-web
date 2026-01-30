package com.sah.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
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
    @Column(nullable = false, name="number_of_games")
    private int number_of_games;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Roles> roles = new HashSet<>();


    public Long getId() {return userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username; }

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public boolean getConfirmed_Account() { return confirmed_account; }
    public void setConfirmed_Account(boolean Confirmed_Account) { this.confirmed_account = Confirmed_Account; }

    public int getNumber_of_games() { return  number_of_games; }
    public void setNumber_of_games(int number_of_games) { this.number_of_games = number_of_games; }

    public Set<Roles> getRoles() { return this.roles; }
    public void SetRoles(Set<Roles> roles) { this.roles = roles; }
}
