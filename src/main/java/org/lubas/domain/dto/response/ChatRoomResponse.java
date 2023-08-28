package org.lubas.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private UserResponse targetUser;
    private ChatMessageResponse message;
}
