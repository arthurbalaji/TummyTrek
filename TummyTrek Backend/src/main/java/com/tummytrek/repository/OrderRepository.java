package com.tummytrek.repository;

import com.tummytrek.entity.Order;
import com.tummytrek.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    List<Order> findByRestaurantId(Long restaurantId);
    
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
    
    List<Order> findByDeliveryPartnerId(Long deliveryPartnerId);
    
    List<Order> findByDeliveryPartnerIdOrderByCreatedAtDesc(Long deliveryPartnerId);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);
    
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalAmountByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    long countByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByCustomer(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByRestaurant(@Param("restaurantId") Long restaurantId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.deliveryPartner.id = :deliveryPartnerId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByDeliveryPartner(@Param("deliveryPartnerId") Long deliveryPartnerId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByCustomerAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findOrdersByRestaurantAndStatus(@Param("restaurantId") Long restaurantId, @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.deliveryPartner.id = :deliveryPartnerId AND o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDeliveryPartnerAndStatuses(@Param("deliveryPartnerId") Long deliveryPartnerId, @Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByRestaurantAndDateRange(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getRestaurantRevenueByDateRange(@Param("restaurantId") Long restaurantId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status IN ('PLACED', 'CONFIRMED', 'PREPARING') ORDER BY o.createdAt ASC")
    List<Order> findPendingOrders();
    
    @Query("SELECT o FROM Order o WHERE o.status = 'READY_FOR_PICKUP' AND o.deliveryPartner IS NULL ORDER BY o.createdAt ASC")
    List<Order> findOrdersReadyForAssignment();
    
    // Additional methods for admin service
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal getTotalRevenueForPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Order> findTop10ByOrderByCreatedAtDesc();
    
    // Vendor-specific methods
    long countByRestaurantId(Long restaurantId);
    
    long countByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime start, LocalDateTime end);
    
    long countByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal getTotalEarningsForRestaurantAndPeriod(@Param("restaurantId") Long restaurantId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.restaurant.id = :restaurantId AND o.status = 'DELIVERED'")
    BigDecimal getTotalEarningsForRestaurant(@Param("restaurantId") Long restaurantId);
    
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);
    
    List<Order> findByRestaurantIdAndStatusOrderByCreatedAtAsc(Long restaurantId, OrderStatus status);
    
    List<Order> findByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime start, LocalDateTime end);
    
    @Query(value = "SELECT DATE(o.created_at) as date, SUM(o.total_amount) as earnings, COUNT(o.id) as orderCount " +
           "FROM orders o WHERE o.restaurant_id = :restaurantId AND o.status = 'DELIVERED' " +
           "AND o.created_at >= CURRENT_DATE - INTERVAL :days DAY GROUP BY DATE(o.created_at) ORDER BY date DESC LIMIT :days", 
           nativeQuery = true)
    List<Object[]> getDailyEarningsForRestaurant(@Param("restaurantId") Long restaurantId, @Param("days") int days);
    
    @Query(value = "SELECT YEARWEEK(o.created_at) as week, SUM(o.total_amount) as earnings, COUNT(o.id) as orderCount " +
           "FROM orders o WHERE o.restaurant_id = :restaurantId AND o.status = 'DELIVERED' " +
           "AND o.created_at >= CURRENT_DATE - INTERVAL :weeks WEEK GROUP BY YEARWEEK(o.created_at) ORDER BY week DESC LIMIT :weeks", 
           nativeQuery = true)
    List<Object[]> getWeeklyEarningsForRestaurant(@Param("restaurantId") Long restaurantId, @Param("weeks") int weeks);
    
    @Query(value = "SELECT CONCAT(YEAR(o.created_at), '-', LPAD(MONTH(o.created_at), 2, '0')) as month, SUM(o.total_amount) as earnings, COUNT(o.id) as orderCount " +
           "FROM orders o WHERE o.restaurant_id = :restaurantId AND o.status = 'DELIVERED' " +
           "AND o.created_at >= CURRENT_DATE - INTERVAL :months MONTH GROUP BY YEAR(o.created_at), MONTH(o.created_at) ORDER BY month DESC LIMIT :months", 
           nativeQuery = true)
    List<Object[]> getMonthlyEarningsForRestaurant(@Param("restaurantId") Long restaurantId, @Param("months") int months);
}
