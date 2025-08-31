package com.tummytrek.controller;

import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.dto.response.VendorDashboardResponse;
import com.tummytrek.dto.response.VendorEarningsResponse;
import com.tummytrek.dto.response.VendorOrderStatsResponse;
import com.tummytrek.dto.request.MenuItemRequest;
import com.tummytrek.entity.Order;
import com.tummytrek.entity.MenuItem;
import com.tummytrek.entity.Restaurant;
import com.tummytrek.entity.User;
import com.tummytrek.entity.Notification;
import com.tummytrek.repository.UserRepository;
import com.tummytrek.repository.RestaurantRepository;
import com.tummytrek.service.VendorService;
import com.tummytrek.service.OrderService;
import com.tummytrek.service.MenuItemService;
import com.tummytrek.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vendor")
@PreAuthorize("hasRole('VENDOR')")
@CrossOrigin(origins = "*")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MenuItemService menuItemService;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testEndpoint(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            return ResponseEntity.ok(ApiResponse.success("Vendor authentication working", "Vendor ID: " + vendorId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<VendorDashboardResponse>> getDashboardStats(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            VendorDashboardResponse dashboard = vendorService.getDashboardStats(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Vendor dashboard retrieved successfully", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving vendor dashboard: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/earnings")
    public ResponseEntity<ApiResponse<List<VendorEarningsResponse>>> getEarningsData(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            List<VendorEarningsResponse> earnings = vendorService.getEarningsData(vendorId, period, days);
            return ResponseEntity.ok(ApiResponse.success("Earnings data retrieved successfully", earnings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving earnings data: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/order-stats")
    public ResponseEntity<ApiResponse<VendorOrderStatsResponse>> getOrderStats(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            VendorOrderStatsResponse orderStats = vendorService.getOrderStats(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Order statistics retrieved successfully", orderStats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving order statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<Order>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Page<Order> orders = vendorService.getRestaurantOrders(vendorId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/pending")
    public ResponseEntity<ApiResponse<List<Order>>> getPendingOrders(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            List<Order> pendingOrders = vendorService.getPendingOrders(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Pending orders retrieved successfully", pendingOrders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving pending orders: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse<Order>> acceptOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) Integer prepTimeMinutes,
            Authentication authentication) {
        try {
            Order order = orderService.updateOrderStatus(orderId, com.tummytrek.entity.OrderStatus.CONFIRMED, "Order accepted by restaurant");
            
            // Send notification with proper parameters
            notificationService.createNotification(
                Notification.NotificationType.ORDER,
                "Order Confirmed",
                "Your order #" + order.getOrderNumber() + " has been confirmed by the restaurant",
                Notification.NotificationPriority.MEDIUM
            );

            return ResponseEntity.ok(ApiResponse.success("Order accepted successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error accepting order: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<ApiResponse<Order>> rejectOrder(
            @PathVariable Long orderId,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            Order order = orderService.updateOrderStatus(orderId, com.tummytrek.entity.OrderStatus.CANCELLED, "Order rejected by restaurant: " + reason);
            
            // Send notification with proper parameters
            notificationService.createNotification(
                Notification.NotificationType.ORDER,
                "Order Cancelled",
                "Your order #" + order.getOrderNumber() + " has been cancelled by the restaurant. Reason: " + reason,
                Notification.NotificationPriority.HIGH
            );

            return ResponseEntity.ok(ApiResponse.success("Order rejected successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error rejecting order: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/ready")
    public ResponseEntity<ApiResponse<Order>> markOrderReady(@PathVariable Long orderId, Authentication authentication) {
        try {
            Order order = orderService.updateOrderStatus(orderId, com.tummytrek.entity.OrderStatus.READY_FOR_PICKUP, "Order is ready for pickup");
            
            // Send notification with proper parameters
            notificationService.createNotification(
                Notification.NotificationType.ORDER,
                "Order Ready",
                "Your order #" + order.getOrderNumber() + " is ready for pickup",
                Notification.NotificationPriority.MEDIUM
            );

            return ResponseEntity.ok(ApiResponse.success("Order marked as ready", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating order status: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/preparing")
    public ResponseEntity<ApiResponse<Order>> markOrderPreparing(@PathVariable Long orderId, Authentication authentication) {
        try {
            Order order = orderService.updateOrderStatus(orderId, com.tummytrek.entity.OrderStatus.PREPARING, "Order is being prepared");
            
            // Send notification with proper parameters
            notificationService.createNotification(
                Notification.NotificationType.ORDER,
                "Order Being Prepared",
                "Your order #" + order.getOrderNumber() + " is being prepared",
                Notification.NotificationPriority.MEDIUM
            );

            return ResponseEntity.ok(ApiResponse.success("Order marked as preparing", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating order status: " + e.getMessage()));
        }
    }

    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurant(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.getRestaurantByVendorId(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant details retrieved successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Restaurant not found: " + e.getMessage()));
        }
    }

    @PostMapping("/restaurant/toggle-status")
    public ResponseEntity<ApiResponse<Restaurant>> toggleRestaurantStatus(
            @RequestParam boolean isOpen,
            @RequestParam boolean isAcceptingOrders,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.updateRestaurantStatus(vendorId, isOpen, isAcceptingOrders);
            return ResponseEntity.ok(ApiResponse.success("Restaurant status updated successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating restaurant status: " + e.getMessage()));
        }
    }

    @GetMapping("/menu/top-selling")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getTopSellingItems(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            List<MenuItem> topItems = vendorService.getTopSellingItems(vendorId, limit);
            return ResponseEntity.ok(ApiResponse.success("Top selling items retrieved successfully", topItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving top selling items: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/orders-by-hour")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getOrdersByHour(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Map<String, Long> ordersByHour = vendorService.getOrdersByHour(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Hourly order data retrieved successfully", ordersByHour));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving hourly order data: " + e.getMessage()));
        }
    }
    
    // Menu Management Endpoints
    @GetMapping("/menu-items")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItems(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.getRestaurantByVendorId(vendorId);
            List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(restaurant.getId());
            return ResponseEntity.ok(ApiResponse.success("Menu items retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving menu items: " + e.getMessage()));
        }
    }
    
    @PostMapping("/menu-items")
    public ResponseEntity<ApiResponse<MenuItem>> addMenuItem(
            @RequestBody MenuItemRequest request,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.getRestaurantByVendorId(vendorId);
            MenuItem menuItem = menuItemService.createMenuItem(restaurant.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Menu item created successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error creating menu item: " + e.getMessage()));
        }
    }
    
    @PutMapping("/menu-items/{id}")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItemRequest request,
            Authentication authentication) {
        try {
            MenuItem menuItem = menuItemService.updateMenuItem(id, request);
            return ResponseEntity.ok(ApiResponse.success("Menu item updated successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating menu item: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/menu-items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            menuItemService.deleteMenuItem(id);
            return ResponseEntity.ok(ApiResponse.success("Menu item deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error deleting menu item: " + e.getMessage()));
        }
    }
    
    @PutMapping("/menu-items/{id}/availability")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItemAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> availabilityData,
            Authentication authentication) {
        try {
            MenuItem menuItem = menuItemService.toggleAvailability(id);
            return ResponseEntity.ok(ApiResponse.success("Menu item availability updated successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating availability: " + e.getMessage()));
        }
    }
    
    // Categories endpoint (returning distinct categories from menu items)
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.getRestaurantByVendorId(vendorId);
            List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(restaurant.getId());
            List<String> categories = menuItems.stream()
                .map(MenuItem::getCategory)
                .filter(category -> category != null && !category.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving categories: " + e.getMessage()));
        }
    }
    
    // Settings endpoints
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorSettings(Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            User vendor = userRepository.findById(vendorId)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            
            Map<String, Object> settings = new HashMap<>();
            settings.put("notifications", Map.of(
                "newOrders", true,
                "orderUpdates", true,
                "paymentNotifications", true,
                "reviewAlerts", true,
                "promotionalEmails", false,
                "systemUpdates", true
            ));
            settings.put("security", Map.of(
                "twoFactorAuth", false,
                "loginAlerts", true,
                "sessionTimeout", 30
            ));
            settings.put("profile", Map.of(
                "name", vendor.getName(),
                "email", vendor.getEmail(),
                "phone", vendor.getPhone()
            ));
            
            return ResponseEntity.ok(ApiResponse.success("Settings retrieved successfully", settings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateVendorProfile(
            @RequestBody Map<String, Object> profileData,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            User vendor = userRepository.findById(vendorId)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
                    
            if (profileData.containsKey("name")) {
                vendor.setName((String) profileData.get("name"));
            }
            if (profileData.containsKey("email")) {
                vendor.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phone")) {
                vendor.setPhone((String) profileData.get("phone"));
            }
            
            userRepository.save(vendor);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", vendor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating profile: " + e.getMessage()));
        }
    }
    
    @PutMapping("/settings/notifications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateNotificationSettings(
            @RequestBody Map<String, Object> settings,
            Authentication authentication) {
        try {
            // In a real implementation, you would save these settings to database
            return ResponseEntity.ok(ApiResponse.success("Notification settings updated successfully", settings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating notification settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/settings/security")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSecuritySettings(
            @RequestBody Map<String, Object> settings,
            Authentication authentication) {
        try {
            // In a real implementation, you would save these settings to database
            return ResponseEntity.ok(ApiResponse.success("Security settings updated successfully", settings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating security settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/settings/payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePaymentSettings(
            @RequestBody Map<String, Object> settings,
            Authentication authentication) {
        try {
            // In a real implementation, you would save these settings to database
            return ResponseEntity.ok(ApiResponse.success("Payment settings updated successfully", settings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating payment settings: " + e.getMessage()));
        }
    }
    
    @PutMapping("/restaurant")
    public ResponseEntity<ApiResponse<Restaurant>> updateRestaurantProfile(
            @RequestBody Map<String, Object> restaurantData,
            Authentication authentication) {
        try {
            Long vendorId = getUserIdFromAuthentication(authentication);
            Restaurant restaurant = vendorService.getRestaurantByVendorId(vendorId);
            
            if (restaurantData.containsKey("name")) {
                restaurant.setName((String) restaurantData.get("name"));
            }
            if (restaurantData.containsKey("description")) {
                restaurant.setDescription((String) restaurantData.get("description"));
            }
            if (restaurantData.containsKey("address")) {
                restaurant.setAddress((String) restaurantData.get("address"));
            }
            if (restaurantData.containsKey("phone")) {
                restaurant.setPhone((String) restaurantData.get("phone"));
            }
            if (restaurantData.containsKey("email")) {
                restaurant.setEmail((String) restaurantData.get("email"));
            }
            if (restaurantData.containsKey("cuisineType")) {
                restaurant.setCuisineType((String) restaurantData.get("cuisineType"));
            }
            if (restaurantData.containsKey("minimumOrderAmount")) {
                restaurant.setMinimumOrderAmount(BigDecimal.valueOf((Double) restaurantData.get("minimumOrderAmount")));
            }
            if (restaurantData.containsKey("deliveryFee")) {
                restaurant.setDeliveryFee(BigDecimal.valueOf((Double) restaurantData.get("deliveryFee")));
            }
            
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);
            return ResponseEntity.ok(ApiResponse.success("Restaurant profile updated successfully", savedRestaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error updating restaurant profile: " + e.getMessage()));
        }
    }

    // Helper method to extract user ID from Authentication
    private Long getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId();
    }
}
