package com.tummytrek.controller;

import com.tummytrek.dto.request.DeliveryPartnerRegistrationRequest;
import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.entity.DeliveryPartner;
import com.tummytrek.entity.DeliveryPartnerStatus;
import com.tummytrek.service.DeliveryPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/delivery-partners")
@CrossOrigin(origins = "*")
public class DeliveryPartnerController {
    
    @Autowired
    private DeliveryPartnerService deliveryPartnerService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<DeliveryPartner>> registerDeliveryPartner(
            @Valid @RequestBody DeliveryPartnerRegistrationRequest request) {
        try {
            DeliveryPartner deliveryPartner = deliveryPartnerService.registerDeliveryPartner(request);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner registered successfully", deliveryPartner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{partnerId}")
    @PreAuthorize("hasRole('DELIVERY_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> getDeliveryPartner(@PathVariable Long partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.getDeliveryPartnerById(partnerId);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner retrieved successfully", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Delivery partner not found: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeliveryPartner>>> getAllDeliveryPartners() {
        try {
            List<DeliveryPartner> partners = deliveryPartnerService.getAllDeliveryPartners();
            return ResponseEntity.ok(ApiResponse.success("Delivery partners retrieved successfully", partners));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving delivery partners: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeliveryPartner>>> getDeliveryPartnersByStatus(
            @PathVariable String status) {
        try {
            DeliveryPartnerStatus partnerStatus = DeliveryPartnerStatus.valueOf(status.toUpperCase());
            List<DeliveryPartner> partners = deliveryPartnerService.getDeliveryPartnersByStatus(partnerStatus);
            return ResponseEntity.ok(ApiResponse.success("Delivery partners retrieved successfully", partners));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error retrieving delivery partners: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDOR')")
    public ResponseEntity<ApiResponse<List<DeliveryPartner>>> getAvailableDeliveryPartners() {
        try {
            List<DeliveryPartner> partners = deliveryPartnerService.getAvailableDeliveryPartners();
            return ResponseEntity.ok(ApiResponse.success("Available delivery partners retrieved successfully", partners));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving available delivery partners: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{partnerId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> approveDeliveryPartner(@PathVariable Long partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.approveDeliveryPartner(partnerId);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner approved successfully", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Approval failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{partnerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> rejectDeliveryPartner(
            @PathVariable Long partnerId, 
            @RequestParam String reason) {
        try {
            DeliveryPartner partner = deliveryPartnerService.rejectDeliveryPartner(partnerId, reason);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner rejected", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Rejection failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{partnerId}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> suspendDeliveryPartner(
            @PathVariable Long partnerId,
            @RequestParam String reason) {
        try {
            DeliveryPartner partner = deliveryPartnerService.suspendDeliveryPartner(partnerId, reason);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner suspended", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Suspension failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{partnerId}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> reactivateDeliveryPartner(@PathVariable Long partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.reactivateDeliveryPartner(partnerId);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner reactivated", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Reactivation failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{partnerId}/location")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> updateLocation(
            @PathVariable Long partnerId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            DeliveryPartner partner = deliveryPartnerService.updateLocation(partnerId, latitude, longitude);
            return ResponseEntity.ok(ApiResponse.success("Location updated successfully", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Location update failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{partnerId}/availability")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public ResponseEntity<ApiResponse<DeliveryPartner>> toggleAvailability(@PathVariable Long partnerId) {
        try {
            DeliveryPartner partner = deliveryPartnerService.toggleAvailability(partnerId);
            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", partner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Availability update failed: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{partnerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDeliveryPartner(@PathVariable Long partnerId) {
        try {
            deliveryPartnerService.deleteDeliveryPartner(partnerId);
            return ResponseEntity.ok(ApiResponse.success("Delivery partner deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Deletion failed: " + e.getMessage()));
        }
    }
}
