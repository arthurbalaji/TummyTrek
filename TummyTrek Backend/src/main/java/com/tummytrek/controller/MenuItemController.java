package com.tummytrek.controller;

import com.tummytrek.dto.request.MenuItemRequest;
import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.entity.MenuItem;
import com.tummytrek.service.MenuItemService;
import com.tummytrek.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/menu-items")
@CrossOrigin(origins = "*")
public class MenuItemController {
    
    @Autowired
    private MenuItemService menuItemService;
    
    @PostMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItem>> createMenuItem(
            @PathVariable Long restaurantId, 
            @Valid @RequestBody MenuItemRequest request) {
        try {
            MenuItem menuItem = menuItemService.createMenuItem(restaurantId, request);
            return ResponseEntity.ok(ApiResponse.success("Menu item created successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Menu item creation failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{menuItemId}")
    public ResponseEntity<ApiResponse<MenuItem>> getMenuItem(@PathVariable Long menuItemId) {
        try {
            MenuItem menuItem = menuItemService.getMenuItemById(menuItemId);
            return ResponseEntity.ok(ApiResponse.success("Menu item retrieved successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Menu item not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getRestaurantMenu(@PathVariable Long restaurantId) {
        try {
            List<MenuItem> menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Menu retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving menu: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/available")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getAvailableMenuItems(@PathVariable Long restaurantId) {
        try {
            List<MenuItem> menuItems = menuItemService.getAvailableMenuItemsByRestaurant(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Available menu items retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving menu items: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/category/{category}")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getMenuItemsByCategory(
            @PathVariable Long restaurantId, 
            @PathVariable String category) {
        try {
            List<MenuItem> menuItems = menuItemService.getMenuItemsByCategory(restaurantId, category);
            return ResponseEntity.ok(ApiResponse.success("Menu items retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving menu items: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/vegetarian")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getVegetarianMenuItems(@PathVariable Long restaurantId) {
        try {
            List<MenuItem> menuItems = menuItemService.getVegetarianMenuItems(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Vegetarian menu items retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving menu items: " + e.getMessage()));
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/popular")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getPopularMenuItems(@PathVariable Long restaurantId) {
        try {
            List<MenuItem> menuItems = menuItemService.getPopularMenuItems(restaurantId);
            return ResponseEntity.ok(ApiResponse.success("Popular menu items retrieved successfully", menuItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving menu items: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MenuItem>>> searchMenuItems(@RequestParam String keyword) {
        try {
            String sanitizedKeyword = ValidationUtil.sanitizeSearchTerm(keyword);
            List<MenuItem> menuItems = menuItemService.searchMenuItems(sanitizedKeyword);
            return ResponseEntity.ok(ApiResponse.success("Menu items found", menuItems));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{menuItemId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItem(
            @PathVariable Long menuItemId, 
            @Valid @RequestBody MenuItemRequest request) {
        try {
            MenuItem menuItem = menuItemService.updateMenuItem(menuItemId, request);
            return ResponseEntity.ok(ApiResponse.success("Menu item updated successfully", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{menuItemId}/toggle-availability")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MenuItem>> toggleAvailability(@PathVariable Long menuItemId) {
        try {
            MenuItem menuItem = menuItemService.toggleAvailability(menuItemId);
            return ResponseEntity.ok(ApiResponse.success("Menu item availability updated", menuItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Availability update failed: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{menuItemId}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long menuItemId) {
        try {
            menuItemService.deleteMenuItem(menuItemId);
            return ResponseEntity.ok(ApiResponse.success("Menu item deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Delete failed: " + e.getMessage()));
        }
    }
}
