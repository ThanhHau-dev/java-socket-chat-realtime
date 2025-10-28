package com.example.chatapp.repository;

import com.example.chatapp.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository để thao tác với bảng room_members
 */
@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    
    /**
     * Lấy danh sách thành viên trong phòng chat
     */
    List<RoomMember> findByRoomId(Long roomId);
    
    /**
     * Lấy danh sách phòng chat của user
     */
    List<RoomMember> findByUserId(Long userId);
    
    /**
     * Kiểm tra user có trong phòng chat không
     */
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
