package com.sah.entity;

import com.sah.enums.LobbyType;
import jakarta.persistence.*;

@Entity
public class LobbyTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int typeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, unique = true)
    LobbyType lobbyType;
}
