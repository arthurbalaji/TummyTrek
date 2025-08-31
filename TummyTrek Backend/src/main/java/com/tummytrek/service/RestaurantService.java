package com.tummytrek.service;

import com.tummytrek.dto.request.RestaurantRegistrationRequest;
import com.tummytrek.entity.Restaurant;
import com.tummytrek.entity.RestaurantStatus;
import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import com.tummytrek.exception.ResourceNotFoundException;
import com.tummytrek.repository.RestaurantRepository;
import com.tummytrek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Restaurant createRestaurant(RestaurantRegistrationRequest request) {
        // Validate business hours
        if (request.getOpeningTime() != null && request.getClosingTime() != null) {
            if (request.getOpeningTime().isAfter(request.getClosingTime())) {
                throw new IllegalArgumentException("Opening time cannot be after closing time");
            }
        }
        
        // Validate coordinates
        if (request.getLatitude() != null && request.getLongitude() != null) {
            if (request.getLatitude() < -90 || request.getLatitude() > 90) {
                throw new IllegalArgumentException("Latitude must be between -90 and 90");
            }
            if (request.getLongitude() < -180 || request.getLongitude() > 180) {
                throw new IllegalArgumentException("Longitude must be between -180 and 180");
            }
        }
        
        // Create vendor user first
        User vendor = new User();
        vendor.setName(request.getOwnerName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setPassword(passwordEncoder.encode(request.getPassword()));
        vendor.setRole(UserRole.VENDOR);
        
        User savedVendor = userRepository.save(vendor);
        
        // Create restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setVendor(savedVendor);
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setMinimumOrderAmount(request.getMinimumOrderAmount());
        restaurant.setDeliveryFee(request.getDeliveryFee());
        restaurant.setFssaiLicense(request.getFssaiLicense());
        restaurant.setGstNumber(request.getGstNumber());
        restaurant.setBankAccountNumber(request.getBankAccountNumber());
        restaurant.setBankName(request.getBankName());
        restaurant.setIfscCode(request.getIfscCode());
        restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL);
        
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
    }
    
    public Restaurant getRestaurantByVendorId(Long vendorId) {
        return restaurantRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for vendor id: " + vendorId));
    }
    
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue();
    }
    
    public List<Restaurant> getApprovedRestaurants() {
        return restaurantRepository.findByStatusAndIsActiveTrue(RestaurantStatus.APPROVED);
    }
    
    public List<Restaurant> getOpenRestaurants() {
        return restaurantRepository.findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrue(RestaurantStatus.APPROVED);
    }
    
    public List<Restaurant> searchRestaurants(String keyword) {
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineTypeContainingIgnoreCaseAndStatusAndIsActiveTrue(
            keyword, keyword, RestaurantStatus.APPROVED);
    }
    
    public List<Restaurant> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeIgnoreCaseAndStatusAndIsActiveTrue(
            cuisineType, RestaurantStatus.APPROVED);
    }
    
    public Restaurant updateRestaurant(Long restaurantId, RestaurantRegistrationRequest request) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        // Update restaurant details
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(request.getAddress());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setMinimumOrderAmount(request.getMinimumOrderAmount());
        restaurant.setDeliveryFee(request.getDeliveryFee());
        
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant approveRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(RestaurantStatus.APPROVED);
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant rejectRestaurant(Long restaurantId, String reason) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(RestaurantStatus.REJECTED);
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant toggleRestaurantStatus(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setIsOpen(!restaurant.getIsOpen());
        return restaurantRepository.save(restaurant);
    }
    
    public Restaurant toggleAcceptingOrders(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setIsAcceptingOrders(!restaurant.getIsAcceptingOrders());
        return restaurantRepository.save(restaurant);
    }
    
    public void deleteRestaurant(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setIsActive(false);
        restaurantRepository.save(restaurant);
    }
    
    public boolean isRestaurantOpen(Long restaurantId) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        LocalTime now = LocalTime.now();
        return restaurant.getIsOpen() && 
               restaurant.getIsAcceptingOrders() &&
               now.isAfter(restaurant.getOpeningTime()) && 
               now.isBefore(restaurant.getClosingTime());
    }
    
    // Missing methods for controllers
    public Page<Restaurant> findAvailableRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrue(RestaurantStatus.APPROVED, pageable);
    }

    public Restaurant findById(Long id) {
        return getRestaurantById(id);
    }

    public List<Restaurant> findByCuisineType(String cuisineType) {
        return getRestaurantsByCuisine(cuisineType);
    }

    public Page<Restaurant> findPopularRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrueOrderByRatingDesc(RestaurantStatus.APPROVED, pageable);
    }

    public Page<Restaurant> findTopRatedRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findByStatusAndIsOpenTrueAndIsAcceptingOrdersTrueAndIsActiveTrueOrderByRatingDesc(RestaurantStatus.APPROVED, pageable);
    }

    public long countByStatus(RestaurantStatus status) {
        return restaurantRepository.countByStatus(status);
    }

    public List<Restaurant> findByStatus(RestaurantStatus status) {
        return restaurantRepository.findByStatus(status);
    }

    public Restaurant updateStatus(Long restaurantId, RestaurantStatus status) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        restaurant.setStatus(status);
        return restaurantRepository.save(restaurant);
    }

    public Page<Restaurant> findAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restaurantRepository.findAll(pageable);
    }
}
