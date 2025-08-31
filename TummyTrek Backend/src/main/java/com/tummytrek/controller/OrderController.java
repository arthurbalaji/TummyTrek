package com.tummytrek.controller;

import com.tummytrek.dto.request.OrderRequest;
import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.entity.Order;
import com.tummytrek.entity.OrderStatus;
import com.tummytrek.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Order creation failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('VENDOR') or hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Order not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber);
            return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Order not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getCustomerOrders(@PathVariable Long customerId) {
        try {
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getRestaurantOrders(@PathVariable Long restaurantId) {
        try {
            List<Order> orders = orderService.getOrdersByRestaurant(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/delivery-partner/{deliveryPartnerId}")
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getDeliveryPartnerOrders(@PathVariable Long deliveryPartnerId) {
        try {
            List<Order> orders = orderService.getOrdersByDeliveryPartner(deliveryPartnerId);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse<List<Order>>> getPendingOrders() {
        try {
            List<Order> orders = orderService.getPendingOrders();
            return ResponseEntity.ok(ApiResponse.success("Pending orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestParam OrderStatus status,
            @RequestParam(required = false) String remarks) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status, remarks);
            return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/assign-delivery-partner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> assignDeliveryPartner(
            @PathVariable Long orderId, 
            @RequestParam Long deliveryPartnerId) {
        try {
            Order order = orderService.assignDeliveryPartner(orderId, deliveryPartnerId);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner assigned successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Assignment failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable Long orderId, 
            @RequestParam String reason,
            @RequestParam String cancelledBy) {
        try {
            Order order = orderService.cancelOrder(orderId, reason, cancelledBy);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Cancellation failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Order>>> getTodayOrders() {
        try {
            List<Order> orders = orderService.getTodayOrders();
            return ResponseEntity.ok(ApiResponse.success("Today's orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/revenue/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BigDecimal>> getTodayRevenue() {
        try {
            BigDecimal revenue = orderService.getTodayRevenue();
            return ResponseEntity.ok(ApiResponse.success("Today's revenue retrieved successfully", revenue));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving revenue: " + e.getMessage()));
        }
    }
}
