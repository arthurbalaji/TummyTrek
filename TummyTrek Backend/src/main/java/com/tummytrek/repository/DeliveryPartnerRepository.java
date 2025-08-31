package com.tummytrek.repository;

import com.tummytrek.entity.DeliveryPartner;
import com.tummytrek.entity.DeliveryPartnerStatus;
import com.tummytrek.entity.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    
    Optional<DeliveryPartner> findByUserId(Long userId);
    
    Optional<DeliveryPartner> findByEmployeeId(String employeeId);
    
    List<DeliveryPartner> findByStatus(DeliveryPartnerStatus status);
    
    List<DeliveryPartner> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);
    
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = 'APPROVED' AND dp.availabilityStatus = 'ONLINE' AND dp.isAvailableForOrders = true AND dp.currentOrderCount < dp.maxConcurrentOrders")
    List<DeliveryPartner> findAvailableDeliveryPartners();
    
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = 'APPROVED' AND dp.availabilityStatus = 'ONLINE' AND dp.isAvailableForOrders = true AND dp.currentOrderCount < dp.maxConcurrentOrders AND (6371000 * acos(cos(radians(:latitude)) * cos(radians(dp.currentLatitude)) * cos(radians(dp.currentLongitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(dp.currentLatitude)))) < :radiusInMeters")
    List<DeliveryPartner> findNearbyAvailableDeliveryPartners(@Param("latitude") Double latitude, @Param("longitude") Double longitude, @Param("radiusInMeters") Double radiusInMeters);
    
    @Query("SELECT dp FROM DeliveryPartner dp WHERE dp.status = :status AND dp.isActive = true ORDER BY dp.rating DESC")
    List<DeliveryPartner> findByStatusOrderByRating(@Param("status") DeliveryPartnerStatus status);
    
    @Query("SELECT COUNT(dp) FROM DeliveryPartner dp WHERE dp.status = :status")
    Long countDeliveryPartnersByStatus(@Param("status") DeliveryPartnerStatus status);
    
    @Query("SELECT COUNT(dp) FROM DeliveryPartner dp WHERE dp.availabilityStatus = :availabilityStatus")
    Long countDeliveryPartnersByAvailabilityStatus(@Param("availabilityStatus") AvailabilityStatus availabilityStatus);
    
    @Query("SELECT dp FROM DeliveryPartner dp WHERE (dp.user.name LIKE %:searchTerm% OR dp.employeeId LIKE %:searchTerm% OR dp.user.phone LIKE %:searchTerm%) AND dp.isActive = true")
    List<DeliveryPartner> searchDeliveryPartners(@Param("searchTerm") String searchTerm);
    
    // Additional method for admin service
    long countByStatus(DeliveryPartnerStatus status);
}
