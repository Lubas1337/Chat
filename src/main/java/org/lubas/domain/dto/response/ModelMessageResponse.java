package org.lubas.domain.dto.response;

import org.lubas.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ModelMessageResponse {
    private String content;
    private String senderUsername;
    private String receiverUsername;
    private ChatRoom chatRoom;
    private Date sendedAt;
    private Boolean isSeen;

}
