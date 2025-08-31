package com.tummytrek.repository;

import com.tummytrek.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByRestaurantId(Long restaurantId);
    
    List<MenuItem> findByRestaurantIdAndIsActiveTrueOrderByCategory(Long restaurantId);
    
    List<MenuItem> findByRestaurantIdAndIsAvailableTrueAndIsActiveTrueOrderByCategory(Long restaurantId);
    
    List<MenuItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrueAndIsAvailableTrue(
        String name, String description);
    
    List<MenuItem> findByRestaurantIdAndCategoryIgnoreCaseAndIsActiveTrueOrderByName(Long restaurantId, String category);
    
    List<MenuItem> findByRestaurantIdAndIsVegetarianTrueAndIsActiveTrueOrderByName(Long restaurantId);
    
    List<MenuItem> findTop10ByRestaurantIdAndIsActiveTrueOrderByTotalOrdersDesc(Long restaurantId);
    
    default List<MenuItem> findAvailableByRestaurant(Long restaurantId) {
        return findByRestaurantIdAndIsAvailableTrueAndIsActiveTrueOrderByCategory(restaurantId);
    }
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> findAvailableMenuItemsByRestaurant(@Param("restaurantId") Long restaurantId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category = :category AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> findByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isVegetarian = :isVegetarian AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> findByRestaurantIdAndVegetarian(@Param("restaurantId") Long restaurantId, @Param("isVegetarian") Boolean isVegetarian);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.price BETWEEN :minPrice AND :maxPrice AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> findByRestaurantIdAndPriceRange(@Param("restaurantId") Long restaurantId, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND (m.name LIKE %:searchTerm% OR m.description LIKE %:searchTerm%) AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> searchMenuItems(@Param("restaurantId") Long restaurantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.isActive = true")
    List<String> findCategoriesByRestaurant(@Param("restaurantId") Long restaurantId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.isActive = true ORDER BY m.totalOrders DESC")
    Page<MenuItem> findPopularMenuItemsByRestaurant(@Param("restaurantId") Long restaurantId, Pageable pageable);
    
    @Query("SELECT m FROM MenuItem m WHERE m.tags LIKE %:tag% AND m.isAvailable = true AND m.isActive = true")
    List<MenuItem> findByTag(@Param("tag") String tag);
    
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true AND m.isActive = true")
    Long countAvailableMenuItemsByRestaurant(@Param("restaurantId") Long restaurantId);
    
    // Additional methods for vendor service
    long countByRestaurantIdAndIsActiveTrue(Long restaurantId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isActive = true ORDER BY m.totalOrders DESC")
    List<MenuItem> findTopByRestaurantIdOrderByTotalOrdersDesc(@Param("restaurantId") Long restaurantId, Pageable pageable);
}
