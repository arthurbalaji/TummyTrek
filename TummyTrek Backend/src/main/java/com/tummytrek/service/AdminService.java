package com.tummytrek.service;

import com.tummytrek.dto.response.DashboardStatsResponse;
import com.tummytrek.dto.response.RevenueDataResponse;
import com.tummytrek.dto.response.OrderStatsResponse;
import com.tummytrek.entity.*;
import com.tummytrek.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    
    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);
        
        // Total counts
        long totalOrders = orderRepository.count();
        long totalCustomers = customerRepository.count();
        long totalRestaurants = restaurantRepository.count();
        long totalDeliveryPartners = deliveryPartnerRepository.count();
        
        // Today's stats
        long todayOrders = orderRepository.countByCreatedAtBetween(todayStart, todayEnd);
        BigDecimal todayRevenue = orderRepository.getTotalRevenueForPeriod(todayStart, todayEnd);
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        
        // Pending approvals
        long pendingRestaurants = restaurantRepository.countByStatus(RestaurantStatus.PENDING_APPROVAL);
        long pendingDeliveryPartners = deliveryPartnerRepository.countByStatus(DeliveryPartnerStatus.PENDING_APPROVAL);
        long pendingApprovals = pendingRestaurants + pendingDeliveryPartners;
        
        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalCustomers(totalCustomers)
                .totalRestaurants(totalRestaurants)
                .totalDeliveryPartners(totalDeliveryPartners)
                .todayOrders(todayOrders)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .pendingApprovals(pendingApprovals)
                .build();
    }
    
    public List<RevenueDataResponse> getRevenueData(String period, int days) {
        List<RevenueDataResponse> revenueData = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1).minusNanos(1);
            
            BigDecimal revenue = orderRepository.getTotalRevenueForPeriod(dayStart, dayEnd);
            long orderCount = orderRepository.countByCreatedAtBetween(dayStart, dayEnd);
            
            revenueData.add(RevenueDataResponse.builder()
                    .date(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                    .orders(orderCount)
                    .build());
        }
        
        return revenueData;
    }
    
    public OrderStatsResponse getOrderStats() {
        List<Order> todayOrders = orderRepository.findByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1)
        );
        
        Map<OrderStatus, Long> statusCounts = todayOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        
        return OrderStatsResponse.builder()
                .placed(statusCounts.getOrDefault(OrderStatus.PLACED, 0L))
                .confirmed(statusCounts.getOrDefault(OrderStatus.CONFIRMED, 0L))
                .preparing(statusCounts.getOrDefault(OrderStatus.PREPARING, 0L))
                .readyForPickup(statusCounts.getOrDefault(OrderStatus.READY_FOR_PICKUP, 0L))
                .pickedUp(statusCounts.getOrDefault(OrderStatus.PICKED_UP, 0L))
                .outForDelivery(statusCounts.getOrDefault(OrderStatus.OUT_FOR_DELIVERY, 0L))
                .delivered(statusCounts.getOrDefault(OrderStatus.DELIVERED, 0L))
                .cancelled(statusCounts.getOrDefault(OrderStatus.CANCELLED, 0L))
                .build();
    }
    
    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public List<Customer> getRecentCustomers(int limit) {
        return customerRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public Map<String, Object> getSystemHealth() {
        return Map.of(
                "status", "healthy",
                "timestamp", LocalDateTime.now(),
                "uptime", "healthy",
                "database", "connected",
                "services", "running"
        );
    }
}
