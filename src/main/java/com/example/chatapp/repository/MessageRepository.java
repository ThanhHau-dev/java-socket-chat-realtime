package com.example.chatapp.repository;

import com.example.chatapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository để thao tác với bảng messages
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Lấy tất cả tin nhắn trong một phòng chat theo thời gian
     */
    List<Message> findByRoomIdOrderBySentAtAsc(Long roomId);
    
    /**
     * Lấy tin nhắn với thông tin người gửi
     */
    @Query("SELECT m FROM Message m WHERE m.roomId = ?1 ORDER BY m.sentAt ASC")
    List<Message> findMessagesByRoomId(Long roomId);
}
