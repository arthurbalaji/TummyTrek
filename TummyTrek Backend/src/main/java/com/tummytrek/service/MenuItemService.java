package com.tummytrek.service;

import com.tummytrek.dto.request.MenuItemRequest;
import com.tummytrek.entity.MenuItem;
import com.tummytrek.entity.Restaurant;
import com.tummytrek.entity.SpiceLevel;
import com.tummytrek.exception.ResourceNotFoundException;
import com.tummytrek.repository.MenuItemRepository;
import com.tummytrek.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuItemService {
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    public MenuItem createMenuItem(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        
        // Validate price
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        
        // Validate discounted price if provided
        if (request.getDiscountedPrice() != null && 
            request.getDiscountedPrice().compareTo(request.getPrice()) >= 0) {
            throw new IllegalArgumentException("Discounted price must be less than original price");
        }
        
        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setDiscountedPrice(request.getDiscountedPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsVegan(request.getIsVegan());
        menuItem.setIsGlutenFree(request.getIsGlutenFree());
        
        // Convert String to SpiceLevel enum
        if (request.getSpiceLevel() != null) {
            try {
                menuItem.setSpiceLevel(SpiceLevel.valueOf(request.getSpiceLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                menuItem.setSpiceLevel(SpiceLevel.MILD); // Default value
            }
        }
        
        menuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        menuItem.setCalories(request.getCalories());
        menuItem.setIngredients(request.getIngredients());
        menuItem.setAllergens(request.getAllergens());
        menuItem.setServingSize(request.getServingSize());
        menuItem.setNutritionalInfo(request.getNutritionalInfo());
        menuItem.setTags(request.getTags());
        
        return menuItemRepository.save(menuItem);
    }
    
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuItemId));
    }
    
    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsActiveTrueOrderByCategory(restaurantId);
    }
    
    public List<MenuItem> getAvailableMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrueAndIsActiveTrueOrderByCategory(restaurantId);
    }
    
    public List<MenuItem> findAvailableByRestaurant(Long restaurantId) {
        return getAvailableMenuItemsByRestaurant(restaurantId);
    }
    
    public List<MenuItem> searchMenuItems(String keyword) {
        return menuItemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrueAndIsAvailableTrue(keyword, keyword);
    }
    
    public List<MenuItem> getMenuItemsByCategory(Long restaurantId, String category) {
        return menuItemRepository.findByRestaurantIdAndCategoryIgnoreCaseAndIsActiveTrueOrderByName(restaurantId, category);
    }
    
    public List<MenuItem> getVegetarianMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsVegetarianTrueAndIsActiveTrueOrderByName(restaurantId);
    }
    
    public List<MenuItem> getPopularMenuItems(Long restaurantId) {
        return menuItemRepository.findTop10ByRestaurantIdAndIsActiveTrueOrderByTotalOrdersDesc(restaurantId);
    }
    
    public MenuItem updateMenuItem(Long menuItemId, MenuItemRequest request) {
        MenuItem menuItem = getMenuItemById(menuItemId);
        
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setDiscountedPrice(request.getDiscountedPrice());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsVegan(request.getIsVegan());
        menuItem.setIsGlutenFree(request.getIsGlutenFree());
        
        // Convert String to SpiceLevel enum for update
        if (request.getSpiceLevel() != null) {
            try {
                menuItem.setSpiceLevel(SpiceLevel.valueOf(request.getSpiceLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                menuItem.setSpiceLevel(SpiceLevel.MILD); // Default value
            }
        }
        
        menuItem.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        menuItem.setCalories(request.getCalories());
        menuItem.setIngredients(request.getIngredients());
        menuItem.setAllergens(request.getAllergens());
        menuItem.setServingSize(request.getServingSize());
        menuItem.setNutritionalInfo(request.getNutritionalInfo());
        menuItem.setTags(request.getTags());
        
        return menuItemRepository.save(menuItem);
    }
    
    public MenuItem toggleAvailability(Long menuItemId) {
        MenuItem menuItem = getMenuItemById(menuItemId);
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        return menuItemRepository.save(menuItem);
    }
    
    public void deleteMenuItem(Long menuItemId) {
        MenuItem menuItem = getMenuItemById(menuItemId);
        menuItem.setIsActive(false);
        menuItemRepository.save(menuItem);
    }
    
    public MenuItem incrementOrderCount(Long menuItemId) {
        MenuItem menuItem = getMenuItemById(menuItemId);
        Integer currentOrders = menuItem.getTotalOrders();
        if (currentOrders == null) currentOrders = 0;
        menuItem.setTotalOrders(currentOrders + 1);
        return menuItemRepository.save(menuItem);
    }
}
