package com.sah.entity;

import jakarta.persistence.*;

@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String role_name;

    public Long getRoleId() { return roleId; }

    public String getRole_name() { return role_name; }
    public void setRole_name(String role_name) {this.role_name = role_name; }
}
