package com.tummytrek.controller;

import com.tummytrek.dto.request.CustomerRegistrationRequest;
import com.tummytrek.dto.response.ApiResponse;
import com.tummytrek.entity.Customer;
import com.tummytrek.entity.CustomerAddress;
import com.tummytrek.service.CustomerService;
import com.tummytrek.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        try {
            Customer customer = customerService.createCustomer(request);
            return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable Long customerId) {
        try {
            ValidationUtil.validateId(customerId);
            Customer customer = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Customer not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByUserId(@PathVariable Long userId) {
        try {
            Customer customer = customerService.getCustomerByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Customer not found: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving customers: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long customerId, 
            @Valid @RequestBody CustomerRegistrationRequest request) {
        try {
            Customer customer = customerService.updateCustomer(customerId, request);
            return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Update failed: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long customerId) {
        try {
            customerService.deleteCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Delete failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{customerId}/loyalty-points/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> addLoyaltyPoints(
            @PathVariable Long customerId, 
            @RequestParam Integer points) {
        try {
            Customer customer = customerService.addLoyaltyPoints(customerId, points);
            return ResponseEntity.ok(ApiResponse.success("Loyalty points added successfully", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to add loyalty points: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{customerId}/loyalty-points/deduct")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> deductLoyaltyPoints(
            @PathVariable Long customerId, 
            @RequestParam Integer points) {
        try {
            Customer customer = customerService.deductLoyaltyPoints(customerId, points);
            return ResponseEntity.ok(ApiResponse.success("Loyalty points deducted successfully", customer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to deduct loyalty points: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{customerId}/addresses")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerAddress>>> getCustomerAddresses(@PathVariable Long customerId) {
        try {
            List<CustomerAddress> addresses = customerService.getCustomerAddresses(customerId);
            return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Error retrieving addresses: " + e.getMessage()));
        }
    }
}
