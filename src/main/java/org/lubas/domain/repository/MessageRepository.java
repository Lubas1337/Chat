package org.lubas.domain.repository;


import org.lubas.domain.entity.ChatRoom;
import org.lubas.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findMessageById(Long id);
    Optional<List<Message>> findMessagesByChatRoom(ChatRoom currChatRoom);
    Optional<Message> findFirstByChatRoomOrderBySendedAt(ChatRoom currChatRoom);
}
