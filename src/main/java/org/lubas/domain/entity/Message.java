package org.lubas.domain.entity;

import lombok.*;

import jakarta.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private AppUser sender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private AppUser receiver;
    private String sendedAt;
    private Boolean isSeen;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    public Message(String content,
                   AppUser sender,
                   AppUser receiver,
                   ChatRoom currChatRoom){
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.chatRoom = currChatRoom;
        this.isSeen = false;
        this.sendedAt = getCurrentDateInFormat();
    }

    public String getCurrentDateInFormat(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormatter.format(new Date());
    }
}
