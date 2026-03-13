package com.sah.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChessLobbyChats {
    @Id
    private String chatId;

    @OneToMany(mappedBy = "chat",  cascade = CascadeType.ALL)
    private List<ChessLobbyChatMessages> messages = new ArrayList<>();

    @Column(nullable = false)
    private boolean reported;
}
