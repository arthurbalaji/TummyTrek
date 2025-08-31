package com.tummytrek.dto.response;

import com.tummytrek.entity.Customer;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerResponse {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer loyaltyPoints;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private String preferredLanguage;
    private String dietaryPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public CustomerResponse() {}
    
    // Constructor with Customer entity
    public CustomerResponse(Customer customer) {
        if (customer != null) {
            this.id = customer.getId();
            this.loyaltyPoints = customer.getLoyaltyPoints();
            this.totalOrders = customer.getTotalOrders();
            this.totalSpent = customer.getTotalSpent();
            this.preferredLanguage = customer.getPreferredLanguage();
            this.dietaryPreferences = customer.getDietaryPreferences();
            this.createdAt = customer.getCreatedAt();
            this.updatedAt = customer.getUpdatedAt();
            
            if (customer.getUser() != null) {
                this.name = customer.getUser().getName();
                this.email = customer.getUser().getEmail();
                this.phone = customer.getUser().getPhone();
            }
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    public String getDietaryPreferences() {
        return dietaryPreferences;
    }
    
    public void setDietaryPreferences(String dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
