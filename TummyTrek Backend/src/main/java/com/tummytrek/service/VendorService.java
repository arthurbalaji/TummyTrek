package com.tummytrek.service;

import com.tummytrek.dto.response.VendorDashboardResponse;
import com.tummytrek.dto.response.VendorEarningsResponse;
import com.tummytrek.dto.response.VendorOrderStatsResponse;
import com.tummytrek.entity.Order;
import com.tummytrek.entity.Restaurant;
import com.tummytrek.entity.MenuItem;
import com.tummytrek.entity.User;
import com.tummytrek.entity.OrderStatus;
import com.tummytrek.exception.ResourceNotFoundException;
import com.tummytrek.repository.OrderRepository;
import com.tummytrek.repository.RestaurantRepository;
import com.tummytrek.repository.MenuItemRepository;
import com.tummytrek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class VendorService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    public VendorDashboardResponse getDashboardStats(Long vendorId) {
        try {
            // Check if vendor has a restaurant
            Restaurant restaurant = restaurantRepository.findByVendorId(vendorId).orElse(null);
            
            if (restaurant == null) {
                // Return default stats for vendor without restaurant
                return VendorDashboardResponse.builder()
                        .restaurantId(null)
                        .restaurantName("No Restaurant Setup")
                        .isOpen(false)
                        .isAcceptingOrders(false)
                        .totalOrders(0L)
                        .totalMenuItems(0L)
                        .todayOrders(0L)
                        .todayEarnings(BigDecimal.ZERO)
                        .weekOrders(0L)
                        .weekEarnings(BigDecimal.ZERO)
                        .monthOrders(0L)
                        .monthEarnings(BigDecimal.ZERO)
                        .totalEarnings(BigDecimal.ZERO)
                        .pendingOrders(0L)
                        .averageRating(0.0)
                        .build();
            }
            
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);
            
            // Total counts
            long totalOrders = orderRepository.countByRestaurantId(restaurant.getId());
            long totalMenuItems = menuItemRepository.countByRestaurantIdAndIsActiveTrue(restaurant.getId());
            
            // Today's stats
            long todayOrders = orderRepository.countByRestaurantIdAndCreatedAtBetween(restaurant.getId(), todayStart, todayEnd);
            BigDecimal todayEarnings = orderRepository.getTotalEarningsForRestaurantAndPeriod(restaurant.getId(), todayStart, todayEnd);
            
            // This week's stats
            LocalDateTime weekStart = LocalDate.now().minusDays(6).atStartOfDay();
            long weekOrders = orderRepository.countByRestaurantIdAndCreatedAtBetween(restaurant.getId(), weekStart, todayEnd);
            BigDecimal weekEarnings = orderRepository.getTotalEarningsForRestaurantAndPeriod(restaurant.getId(), weekStart, todayEnd);
            
            // This month's stats
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            long monthOrders = orderRepository.countByRestaurantIdAndCreatedAtBetween(restaurant.getId(), monthStart, todayEnd);
            BigDecimal monthEarnings = orderRepository.getTotalEarningsForRestaurantAndPeriod(restaurant.getId(), monthStart, todayEnd);
            
            // Total earnings
            BigDecimal totalEarnings = orderRepository.getTotalEarningsForRestaurant(restaurant.getId());
            
            // Pending orders
            long pendingOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.PLACED);
            
            // Average rating
            double averageRating = restaurant.getRating() != null ? restaurant.getRating().doubleValue() : 4.5;
            
            return VendorDashboardResponse.builder()
                    .restaurantId(restaurant.getId())
                    .restaurantName(restaurant.getName())
                    .isOpen(restaurant.getIsOpen() != null ? restaurant.getIsOpen() : true)
                    .isAcceptingOrders(restaurant.getIsAcceptingOrders() != null ? restaurant.getIsAcceptingOrders() : true)
                    .totalOrders(totalOrders)
                    .totalMenuItems(totalMenuItems)
                    .todayOrders(todayOrders)
                    .todayEarnings(todayEarnings != null ? todayEarnings : BigDecimal.ZERO)
                    .weekOrders(weekOrders)
                    .weekEarnings(weekEarnings != null ? weekEarnings : BigDecimal.ZERO)
                    .monthOrders(monthOrders)
                    .monthEarnings(monthEarnings != null ? monthEarnings : BigDecimal.ZERO)
                    .totalEarnings(totalEarnings != null ? totalEarnings : BigDecimal.ZERO)
                    .pendingOrders(pendingOrders)
                    .averageRating(averageRating)
                    .build();
        } catch (Exception e) {
            // Log the error and return default values
            System.err.println("Error in getDashboardStats: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching dashboard stats: " + e.getMessage(), e);
        }
    }

    public List<VendorEarningsResponse> getEarningsData(Long vendorId, String period, int days) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        
        return switch (period) {
            case "week" -> getWeeklyEarnings(restaurant.getId(), days);
            case "month" -> getMonthlyEarnings(restaurant.getId(), days);
            default -> getDailyEarnings(restaurant.getId(), days);
        };
    }

    public VendorOrderStatsResponse getOrderStats(Long vendorId) {
        try {
            Restaurant restaurant = getRestaurantByVendorId(vendorId);
            
            long placedOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.PLACED);
            long confirmedOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.CONFIRMED);
            long preparingOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.PREPARING);
            long readyOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.READY_FOR_PICKUP);
            long pickedUpOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.PICKED_UP);
            long deliveredOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.DELIVERED);
            long cancelledOrders = orderRepository.countByRestaurantIdAndStatus(restaurant.getId(), OrderStatus.CANCELLED);
            
            return VendorOrderStatsResponse.builder()
                    .placed(placedOrders)
                    .confirmed(confirmedOrders)
                    .preparing(preparingOrders)
                    .ready(readyOrders)
                    .pickedUp(pickedUpOrders)
                    .delivered(deliveredOrders)
                    .cancelled(cancelledOrders)
                    .build();
        } catch (Exception e) {
            System.err.println("Error in getOrderStats: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching order stats: " + e.getMessage(), e);
        }
    }

    public Page<Order> getRestaurantOrders(Long vendorId, int page, int size) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByRestaurantId(restaurant.getId(), pageable);
    }

    public List<Order> getPendingOrders(Long vendorId) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        return orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtAsc(restaurant.getId(), OrderStatus.PLACED);
    }

    public List<MenuItem> getTopSellingItems(Long vendorId, int limit) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        Pageable pageable = PageRequest.of(0, limit);
        return menuItemRepository.findTopByRestaurantIdOrderByTotalOrdersDesc(restaurant.getId(), pageable);
    }

    public Map<String, Long> getOrdersByHour(Long vendorId) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        
        List<Order> todayOrders = orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurant.getId(), startOfDay, endOfDay);
        
        return todayOrders.stream()
                .collect(Collectors.groupingBy(
                    order -> String.format("%02d:00", order.getCreatedAt().getHour()),
                    Collectors.counting()
                ));
    }

    public Restaurant getRestaurantByVendorId(Long vendorId) {
        return restaurantRepository.findByVendorId(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for vendor id: " + vendorId));
    }

    public Restaurant updateRestaurantStatus(Long vendorId, boolean isOpen, boolean isAcceptingOrders) {
        Restaurant restaurant = getRestaurantByVendorId(vendorId);
        restaurant.setIsOpen(isOpen);
        restaurant.setIsAcceptingOrders(isAcceptingOrders);
        return restaurantRepository.save(restaurant);
    }

    // Helper methods for earnings calculation
    private List<VendorEarningsResponse> getDailyEarnings(Long restaurantId, int days) {
        return orderRepository.getDailyEarningsForRestaurant(restaurantId, days)
                .stream()
                .map(result -> VendorEarningsResponse.builder()
                        .date((String) result[0])
                        .earnings((BigDecimal) result[1])
                        .orderCount((Long) result[2])
                        .build())
                .collect(Collectors.toList());
    }

    private List<VendorEarningsResponse> getWeeklyEarnings(Long restaurantId, int weeks) {
        return orderRepository.getWeeklyEarningsForRestaurant(restaurantId, weeks)
                .stream()
                .map(result -> VendorEarningsResponse.builder()
                        .date((String) result[0])
                        .earnings((BigDecimal) result[1])
                        .orderCount((Long) result[2])
                        .build())
                .collect(Collectors.toList());
    }

    private List<VendorEarningsResponse> getMonthlyEarnings(Long restaurantId, int months) {
        return orderRepository.getMonthlyEarningsForRestaurant(restaurantId, months)
                .stream()
                .map(result -> VendorEarningsResponse.builder()
                        .date((String) result[0])
                        .earnings((BigDecimal) result[1])
                        .orderCount((Long) result[2])
                        .build())
                .collect(Collectors.toList());
    }
}
