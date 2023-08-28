package org.lubas.service;


import org.lubas.domain.entity.AppUser;
import org.lubas.domain.entity.ChatRoom;
import org.lubas.domain.dto.response.ChatMessageResponse;
import org.lubas.domain.dto.response.ChatRoomResponse;
import org.lubas.domain.dto.response.UserResponse;
import org.lubas.domain.repository.ChatRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@AllArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final MessageService messageService;

    public ChatRoom createChatRoom(List<AppUser> memberList){
        ChatRoom chatRoom = new ChatRoom(memberList);
        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomResponse> listChatRoomsByGivenUser(AppUser appUser){
        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByMembersContains(appUser)
                .orElse(new ArrayList<>());

        List<ChatRoomResponse> chatRoomResponseList = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            List<AppUser> currMemberList = chatRoom.getMembers();
            for(AppUser x : currMemberList){
                if(x != appUser){
                    UserResponse newUserResponse = new UserResponse(
                            x.getUsername(),
                            x.getLastSeenAt()
                    );
                    ChatMessageResponse lastMessage = messageService.getLastSendedMessageByChatRoom(chatRoom);
                    chatRoomResponseList.add(new ChatRoomResponse(
                            chatRoom.getId(),
                            newUserResponse,
                            lastMessage
                    ));
                }
            }
        }

        return chatRoomResponseList;
    }

    public ChatRoom findChatRoomById(Long chatRoomId){
        return chatRoomRepository.findChatRoomById(chatRoomId)
                .orElseThrow(NullPointerException::new);
    }
    public ChatRoomResponse findChatRoom(AppUser hostAppUser, AppUser givenAppUser){

        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByMembersContains(hostAppUser)
                .orElse(new ArrayList<>());

        UserResponse newUserResponse = new UserResponse(
                givenAppUser.getUsername(),
                givenAppUser.getLastSeenAt()
        );

        for(ChatRoom x: chatRoomList){
            if(x.getMembers().contains(givenAppUser)){
                ChatMessageResponse lastMessage = messageService.getLastSendedMessageByChatRoom(x);
                return new ChatRoomResponse(x.getId(), newUserResponse, lastMessage);
            }
        }
        ChatRoom createdChatRoom = createChatRoom(Arrays.asList(hostAppUser, givenAppUser));
        return new ChatRoomResponse(createdChatRoom.getId(), newUserResponse, null);
    }

}
