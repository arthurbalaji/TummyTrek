package com.tummytrek.service;

import com.tummytrek.dto.request.DeliveryPartnerRegistrationRequest;
import com.tummytrek.entity.*;
import com.tummytrek.repository.DeliveryPartnerRepository;
import com.tummytrek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeliveryPartnerService {
    
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public DeliveryPartner registerDeliveryPartner(DeliveryPartnerRegistrationRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("User with this phone number already exists");
        }
        
        // Create user first
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.DELIVERY_PARTNER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Create delivery partner
        DeliveryPartner deliveryPartner = new DeliveryPartner();
        deliveryPartner.setUser(savedUser);
        deliveryPartner.setEmployeeId(generateEmployeeId());
        deliveryPartner.setVehicleType(VehicleType.valueOf(request.getVehicleType().toUpperCase()));
        deliveryPartner.setVehicleNumber(request.getVehicleNumber());
        deliveryPartner.setLicenseNumber(request.getLicenseNumber());
        deliveryPartner.setStatus(DeliveryPartnerStatus.PENDING_APPROVAL);
        deliveryPartner.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
        deliveryPartner.setIsAvailableForOrders(false);
        deliveryPartner.setCurrentOrderCount(0);
        deliveryPartner.setMaxConcurrentOrders(3);
        deliveryPartner.setTotalDeliveries(0);
        deliveryPartner.setRating(java.math.BigDecimal.ZERO);
        deliveryPartner.setTotalReviews(0);
        deliveryPartner.setCreatedAt(LocalDateTime.now());
        deliveryPartner.setUpdatedAt(LocalDateTime.now());
        
        return deliveryPartnerRepository.save(deliveryPartner);
    }
    
    public DeliveryPartner getDeliveryPartnerById(Long id) {
        return deliveryPartnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delivery partner not found with id: " + id));
    }
    
    public List<DeliveryPartner> getAllDeliveryPartners() {
        return deliveryPartnerRepository.findAll();
    }
    
    public List<DeliveryPartner> getDeliveryPartnersByStatus(DeliveryPartnerStatus status) {
        return deliveryPartnerRepository.findByStatus(status);
    }
    
    public List<DeliveryPartner> getAvailableDeliveryPartners() {
        return deliveryPartnerRepository.findAvailableDeliveryPartners();
    }
    
    public DeliveryPartner approveDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setStatus(DeliveryPartnerStatus.APPROVED);
        partner.setIsAvailableForOrders(true);
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public DeliveryPartner rejectDeliveryPartner(Long partnerId, String reason) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setStatus(DeliveryPartnerStatus.REJECTED);
        partner.setIsAvailableForOrders(false);
        // Note: Add rejectionReason field to entity if needed
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public DeliveryPartner suspendDeliveryPartner(Long partnerId, String reason) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setStatus(DeliveryPartnerStatus.SUSPENDED);
        partner.setIsAvailableForOrders(false);
        partner.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
        // Note: Add suspensionReason field to entity if needed
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public DeliveryPartner reactivateDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setStatus(DeliveryPartnerStatus.APPROVED);
        partner.setIsAvailableForOrders(true);
        // Note: Clear suspensionReason if field exists
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public DeliveryPartner updateLocation(Long partnerId, Double latitude, Double longitude) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setCurrentLatitude(latitude);
        partner.setCurrentLongitude(longitude);
        partner.setLastLocationUpdate(LocalDateTime.now());
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public DeliveryPartner toggleAvailability(Long partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        
        if (partner.getAvailabilityStatus() == AvailabilityStatus.ONLINE) {
            partner.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
            partner.setIsAvailableForOrders(false);
        } else {
            partner.setAvailabilityStatus(AvailabilityStatus.ONLINE);
            partner.setIsAvailableForOrders(partner.getStatus() == DeliveryPartnerStatus.APPROVED);
        }
        
        partner.setUpdatedAt(LocalDateTime.now());
        return deliveryPartnerRepository.save(partner);
    }
    
    public void deleteDeliveryPartner(Long partnerId) {
        DeliveryPartner partner = getDeliveryPartnerById(partnerId);
        partner.setIsAvailableForOrders(false);
        partner.setStatus(DeliveryPartnerStatus.TERMINATED);
        partner.setUpdatedAt(LocalDateTime.now());
        deliveryPartnerRepository.save(partner);
    }
    
    private String generateEmployeeId() {
        return "DP" + System.currentTimeMillis();
    }
}