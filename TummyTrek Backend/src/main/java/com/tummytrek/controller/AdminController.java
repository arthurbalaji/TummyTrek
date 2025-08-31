package com.tummytrek.controller;

import com.tummytrek.dto.response.*;
import com.tummytrek.entity.Customer;
import com.tummytrek.entity.Order;
import com.tummytrek.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        try {
            DashboardStatsResponse stats = adminService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving dashboard stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard/revenue")
    public ResponseEntity<ApiResponse<List<RevenueDataResponse>>> getRevenueData(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<RevenueDataResponse> revenueData = adminService.getRevenueData(period, days);
            return ResponseEntity.ok(ApiResponse.success("Revenue data retrieved successfully", revenueData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving revenue data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/dashboard/orders/stats")
    public ResponseEntity<ApiResponse<OrderStatsResponse>> getOrderStats() {
        try {
            OrderStatsResponse orderStats = adminService.getOrderStats();
            return ResponseEntity.ok(ApiResponse.success("Order stats retrieved successfully", orderStats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving order stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/orders/recent")
    public ResponseEntity<ApiResponse<List<Order>>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Order> orders = adminService.getRecentOrders(limit);
            return ResponseEntity.ok(ApiResponse.success("Recent orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving recent orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/customers/recent")
    public ResponseEntity<ApiResponse<List<Customer>>> getRecentCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Customer> customers = adminService.getRecentCustomers(limit);
            return ResponseEntity.ok(ApiResponse.success("Recent customers retrieved successfully", customers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving recent customers: " + e.getMessage()));
        }
    }
    
    @GetMapping("/system/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        try {
            Map<String, Object> health = adminService.getSystemHealth();
            return ResponseEntity.ok(ApiResponse.success("System health retrieved successfully", health));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving system health: " + e.getMessage()));
        }
    }
}