package com.example.chatapp.repository;

import com.example.chatapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository để thao tác với bảng users
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Tìm user theo username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean existsByUsername(String username);
    
    /**
     * Tìm kiếm user theo username (không phân biệt hoa thường)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * Tìm kiếm user theo full name
     */
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    
    /**
     * Tìm kiếm user theo username hoặc full name
     */
    List<User> findByUsernameContainingOrFullNameContaining(String username, String fullName);
}
