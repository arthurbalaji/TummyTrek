package com.tummytrek.repository;

import com.tummytrek.entity.Restaurant;
import com.tummytrek.entity.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    Optional<Restaurant> findByVendorId(Long vendorId);
    
    List<Restaurant> findByStatus(RestaurantStatus status);
    
    List<Restaurant> findByIsActiveTrue();
    
    List<Restaurant> findByStatusAndIsActiveTrue(RestaurantStatus status);
    
    List<Restaurant> findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrue(RestaurantStatus status);
    
    Page<Restaurant> findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrue(RestaurantStatus status, Pageable pageable);
    
    Page<Restaurant> findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrueOrderByRatingDesc(RestaurantStatus status, Pageable pageable);
    
    List<Restaurant> findByNameContainingIgnoreCaseOrCuisineTypeContainingIgnoreCaseAndStatusAndIsActiveTrue(
        String name, String cuisineType, RestaurantStatus status);
    
    List<Restaurant> findByCuisineTypeIgnoreCaseAndStatusAndIsActiveTrue(String cuisineType, RestaurantStatus status);
    
    long countByStatus(RestaurantStatus status);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = :status AND r.isActive = true AND r.isOpen = true AND r.isAcceptingOrders = true")
    List<Restaurant> findAvailableRestaurantsByStatus(@Param("status") RestaurantStatus status);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true AND r.isAcceptingOrders = true")
    Page<Restaurant> findAvailableRestaurants(Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE r.cuisineType LIKE %:cuisineType% AND r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true")
    List<Restaurant> findByCuisineType(@Param("cuisineType") String cuisineType);
    
    @Query("SELECT r FROM Restaurant r WHERE (r.name LIKE %:searchTerm% OR r.cuisineType LIKE %:searchTerm%) AND r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true")
    List<Restaurant> searchRestaurants(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :minRating AND r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true ORDER BY r.rating DESC")
    List<Restaurant> findByMinRating(@Param("minRating") BigDecimal minRating);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * cos(radians(r.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(r.latitude)))) < (:radiusInKm)")
    List<Restaurant> findNearbyRestaurants(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("radiusInKm") Double radiusInKm);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true ORDER BY r.totalOrders DESC")
    Page<Restaurant> findPopularRestaurants(Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE r.status = 'APPROVED' AND r.isActive = true AND r.isOpen = true ORDER BY r.rating DESC, r.totalReviews DESC")
    Page<Restaurant> findTopRatedRestaurants(Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.status = :status")
    Long countRestaurantsByStatus(@Param("status") RestaurantStatus status);
}
