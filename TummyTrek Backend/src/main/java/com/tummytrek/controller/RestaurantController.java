package com.tummytrek.controller;

import com.tummytrek.dto.request.RestaurantRegistrationRequest;
import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.entity.Restaurant;
import com.tummytrek.service.RestaurantService;
import com.tummytrek.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {
    
    @Autowired
    private RestaurantService restaurantService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Restaurant>> registerRestaurant(@Valid @RequestBody RestaurantRegistrationRequest request) {
        try {
            Restaurant restaurant = restaurantService.createRestaurant(request);
            return ResponseEntity.ok(ApiResponse.success("Restaurant registered successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{restaurantId}")
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurant(@PathVariable Long restaurantId) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant retrieved successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Restaurant not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurantByVendor(@PathVariable Long vendorId) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantByVendorId(vendorId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant retrieved successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Restaurant not found: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Restaurant>>> getAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getApprovedRestaurants();
            return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", restaurants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving restaurants: " + e.getMessage()));
        }
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Restaurant>>> getAllRestaurantsForAdmin() {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", restaurants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving restaurants: " + e.getMessage()));
        }
    }
    
    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<Restaurant>>> getOpenRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getOpenRestaurants();
            return ResponseEntity.ok(ApiResponse.success("Open restaurants retrieved successfully", restaurants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving restaurants: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Restaurant>>> searchRestaurants(@RequestParam String keyword) {
        try {
            String sanitizedKeyword = ValidationUtil.sanitizeSearchTerm(keyword);
            List<Restaurant> restaurants = restaurantService.searchRestaurants(sanitizedKeyword);
            return ResponseEntity.ok(ApiResponse.success("Restaurants found", restaurants));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<ApiResponse<List<Restaurant>>> getRestaurantsByCuisine(@PathVariable String cuisineType) {
        try {
            List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType);
            return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", restaurants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving restaurants: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> updateRestaurant(
            @PathVariable Long restaurantId, 
            @Valid @RequestBody RestaurantRegistrationRequest request) {
        try {
            Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, request);
            return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{restaurantId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> approveRestaurant(@PathVariable Long restaurantId) {
        try {
            Restaurant restaurant = restaurantService.approveRestaurant(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant approved successfully", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Approval failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{restaurantId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> rejectRestaurant(
            @PathVariable Long restaurantId, 
            @RequestParam String reason) {
        try {
            Restaurant restaurant = restaurantService.rejectRestaurant(restaurantId, reason);
            return ResponseEntity.ok(ApiResponse.success("Restaurant rejected", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Rejection failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{restaurantId}/toggle-status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> toggleRestaurantStatus(@PathVariable Long restaurantId) {
        try {
            Restaurant restaurant = restaurantService.toggleRestaurantStatus(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant status updated", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{restaurantId}/toggle-orders")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Restaurant>> toggleAcceptingOrders(@PathVariable Long restaurantId) {
        try {
            Restaurant restaurant = restaurantService.toggleAcceptingOrders(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Order acceptance status updated", restaurant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(@PathVariable Long restaurantId) {
        try {
            restaurantService.deleteRestaurant(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Delete failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{restaurantId}/is-open")
    public ResponseEntity<ApiResponse<Boolean>> isRestaurantOpen(@PathVariable Long restaurantId) {
        try {
            boolean isOpen = restaurantService.isRestaurantOpen(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Restaurant status checked", isOpen));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error checking restaurant status: " + e.getMessage()));
        }
    }
}
