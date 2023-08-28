package org.lubas.domain.repository;


import org.lubas.domain.entity.AppUser;
import org.lubas.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findChatRoomById(Long id);
    Optional<List<ChatRoom>> findChatRoomsByMembersContains(AppUser appUser);

}
