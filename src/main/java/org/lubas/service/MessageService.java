package org.lubas.service;

import org.lubas.domain.entity.AppUser;
import org.lubas.domain.entity.ChatRoom;
import org.lubas.domain.entity.Message;
import org.lubas.domain.dto.response.ChatMessageResponse;
import org.lubas.domain.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;


    public Message createMessage(String content,
                                 AppUser senderAppUser,
                                 AppUser receiverAppUser,
                                 ChatRoom currChatRoom){
        Message createdMessage = new Message(
                content,
                senderAppUser,
                receiverAppUser,
                currChatRoom
        );
        return messageRepository.save(createdMessage);
    }

    public List<ChatMessageResponse> getMessageByChatRoom(ChatRoom currChatRoom){
        List<Message> messageList = messageRepository.findMessagesByChatRoom(currChatRoom)
                .orElse(new ArrayList<>());
        List<ChatMessageResponse> messageResponseList = new ArrayList<>();
        for(Message message : messageList){
            messageResponseList.add(
                    new ChatMessageResponse(
                            message.getContent(),
                            message.getSender().getUsername(),
                            message.getReceiver().getUsername(),
                            message.getChatRoom().getId().intValue(),
                            message.getSendedAt(),
                            message.getIsSeen()
                    )
            );
        }

        return messageResponseList;
    }

    public ChatMessageResponse getLastSendedMessageByChatRoom(ChatRoom currChatRoom){
        Message lastMessage =  messageRepository.findFirstByChatRoomOrderBySendedAt(currChatRoom)
                .orElse(null);
        if(lastMessage != null) {
            return new ChatMessageResponse(
                    lastMessage.getContent(),
                    lastMessage.getSender().getUsername(),
                    lastMessage.getReceiver().getUsername(),
                    lastMessage.getChatRoom().getId().intValue(),
                    lastMessage.getSendedAt(),
                    lastMessage.getIsSeen()
            );
        }
        return null;
    }
}
