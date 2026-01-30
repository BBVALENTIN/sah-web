package com.sah.entity;

import jakarta.persistence.*;

public class Chess_lobby {
    @Id
    private String lobby_Id;

    @Column(nullable = false, name = "format")
    private String format;

    public String getLobby_Id() { return lobby_Id; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
