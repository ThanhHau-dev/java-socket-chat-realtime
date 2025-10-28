package com.example.chatapp.repository;

import com.example.chatapp.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository để thao tác với bảng chat_rooms
 */
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    /**
     * Lấy danh sách phòng chat mà user tham gia
     */
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id IN " +
           "(SELECT rm.roomId FROM RoomMember rm WHERE rm.userId = ?1)")
    List<ChatRoom> findRoomsByUserId(Long userId);
    
    /**
     * Tìm phòng chat private giữa 2 user - sửa query để match với database
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr WHERE cr.roomType = 'private' " +
           "AND cr.id IN (SELECT rm1.roomId FROM RoomMember rm1 WHERE rm1.userId = ?1) " +
           "AND cr.id IN (SELECT rm2.roomId FROM RoomMember rm2 WHERE rm2.userId = ?2)")
    List<ChatRoom> findPrivateRoomBetweenUsers(Long userId1, Long userId2);
}
