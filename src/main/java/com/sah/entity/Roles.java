package com.sah.entity;

import com.sah.enums.RoleType;
import jakarta.persistence.*;

@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    public Long getRoleId() { return roleId; }

    public RoleType getName() { return name; }
    public void setRole_name(RoleType name) {this.name = name; }
}
