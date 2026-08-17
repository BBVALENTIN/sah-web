package com.sah.dto.misc;

import com.sah.enums.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDTO {
    private String sender;
    private String content;
    private MessageType type;
}
