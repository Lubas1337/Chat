package org.lubas.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageResponse {
    private String content;
    private String senderUsername;
    private String receiverUsername;
    private Integer roomId;
    private String sendedAt;
    private Boolean isSeen;
}
