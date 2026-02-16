package com.sah.dto;

import com.sah.enums.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String content;
    private String sender; // will change to User
    private MessageType type;
}
