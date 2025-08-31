package com.tummytrek.repository;

import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByEmailOrPhone(String email, String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    List<User> findByRole(UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE (u.email LIKE %:searchTerm% OR u.name LIKE %:searchTerm% OR u.phone LIKE %:searchTerm%) AND u.isActive = true")
    List<User> searchActiveUsers(@Param("searchTerm") String searchTerm);
}
