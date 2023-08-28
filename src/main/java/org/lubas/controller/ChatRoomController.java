package org.lubas.controller;


import org.lubas.domain.entity.ChatRoom;
import org.lubas.domain.entity.Message;
import org.lubas.domain.entity.AppUser;
import org.lubas.domain.dto.request.MessageRequest;
import org.lubas.domain.dto.response.ChatMessageResponse;
import org.lubas.domain.dto.response.ChatRoomResponse;
import org.lubas.service.ChatRoomService;
import org.lubas.service.MessageService;
import org.lubas.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@Controller
@AllArgsConstructor
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);

    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/joinMessage")
    public ResponseEntity<?> receiveJoinMessage(@Payload String userUsername, SimpMessageHeaderAccessor headerAccessor){
        logger.info(userUsername + " is connected");
        AppUser currAppUser = userService.loadUserByUsername(userUsername);
        userService.updateLastSeenAt(currAppUser, "Online");
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("User" , currAppUser);
        return ResponseEntity
                .ok()
                .body(userUsername + " is connected");
    }


    @MessageMapping("/message")
    public ChatMessageResponse recMessage(@Payload MessageRequest messageRequest){
        AppUser senderAppUser = userService.loadUserByUsername(messageRequest.getSenderUsername());
        AppUser receiverAppUser = userService.loadUserByUsername(messageRequest.getReceiverUsername());
        ChatRoom currChatRoom = chatRoomService.findChatRoomById(Long.valueOf(messageRequest.getRoomId()));

        Message createdMessage = messageService.createMessage(
                messageRequest.getContent(),
                senderAppUser,
                receiverAppUser,
                currChatRoom
        );

        ChatMessageResponse createdMessageResponse = new ChatMessageResponse(
                createdMessage.getContent(),
                createdMessage.getSender().getUsername(),
                createdMessage.getReceiver().getUsername(),
                createdMessage.getChatRoom().getId().intValue(),
                createdMessage.getSendedAt(),
                createdMessage.getIsSeen()
        );

        messagingTemplate.convertAndSend("/chatroom/" + messageRequest.getSenderUsername() + "/message", createdMessageResponse);
        messagingTemplate.convertAndSend("/chatroom/" + messageRequest.getReceiverUsername() + "/message", createdMessageResponse);

        return createdMessageResponse;
    }

    @GetMapping(value="/chat/getMessages/{room_id}")
    public ResponseEntity<List<ChatMessageResponse>> getMessagesByChatRoom(@PathVariable("room_id") Integer chatRoomId){
        ChatRoom currChatRoom = chatRoomService.findChatRoomById(Long.valueOf(chatRoomId));
        List<ChatMessageResponse> messageList = messageService.getMessageByChatRoom(currChatRoom);
        return ResponseEntity
                .ok()
                .body(messageList);
    }

    @GetMapping(value="/chat/getListRoomsByUser")
    public ResponseEntity<?> getAllChatRoomsByGivenUser(@RequestHeader("Authorization") String jwtToken){
        AppUser appUser = userService.findUserFromJwtToken(jwtToken);
        if(appUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }

        List<ChatRoomResponse> chatRoomResponseList = chatRoomService.listChatRoomsByGivenUser(
                appUser
        );

        return ResponseEntity
                .ok()
                .body(chatRoomResponseList);
    }

    @GetMapping(value="/chat/findRoom/{username}")
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String jwtToken,
                                        @PathVariable("username") String username){
        AppUser hostAppUser = userService.findUserFromJwtToken(jwtToken);
        if(hostAppUser == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Jwt Token");
        }
        AppUser givenAppUser = userService.loadUserByUsername(username);

        ChatRoomResponse chatRoomResponse = chatRoomService.findChatRoom(hostAppUser, givenAppUser);
        return ResponseEntity
                .ok()
                .body(chatRoomResponse);
    }
}
