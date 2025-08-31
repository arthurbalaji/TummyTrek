package com.tummytrek.repository;

import com.tummytrek.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByUserId(Long userId);
    
    Optional<Customer> findByUserEmail(String email);
    
    Optional<Customer> findByUserPhone(String phone);
    
    @Query("SELECT c FROM Customer c WHERE c.user.status = 'ACTIVE'")
    List<Customer> findByUser_ActiveTrue();
    
    default List<Customer> findByIsActiveTrue() {
        return findByUser_ActiveTrue();
    }
    
    @Query("SELECT c FROM Customer c WHERE c.user.status = 'ACTIVE' ORDER BY c.totalOrders DESC")
    Page<Customer> findTopCustomersByOrders(Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.user.status = 'ACTIVE' ORDER BY c.totalSpent DESC")
    Page<Customer> findTopCustomersBySpending(Pageable pageable);
    
    @Query("SELECT c FROM Customer c WHERE c.totalSpent >= :minAmount AND c.user.status = 'ACTIVE'")
    List<Customer> findCustomersWithMinSpending(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints AND c.user.status = 'ACTIVE'")
    List<Customer> findCustomersWithMinLoyaltyPoints(@Param("minPoints") Integer minPoints);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.user.status = 'ACTIVE'")
    Long countActiveCustomers();
    
    @Query("SELECT c FROM Customer c WHERE (c.user.name LIKE %:searchTerm% OR c.user.email LIKE %:searchTerm% OR c.user.phone LIKE %:searchTerm%) AND c.user.status = 'ACTIVE'")
    List<Customer> searchCustomers(@Param("searchTerm") String searchTerm);
    
    // Additional method for admin service
    List<Customer> findTop10ByOrderByCreatedAtDesc();
}
