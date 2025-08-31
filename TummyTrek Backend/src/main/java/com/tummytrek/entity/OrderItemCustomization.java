package com.tummytrek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item_customizations")
public class OrderItemCustomization extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
    
    @NotBlank(message = "Customization name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "value")
    private String value;
    
    @NotNull(message = "Additional price is required")
    @DecimalMin(value = "0.0", message = "Additional price cannot be negative")
    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    
    // Default constructor
    public OrderItemCustomization() {}
    
    // Constructor
    public OrderItemCustomization(OrderItem orderItem, String name, String value, BigDecimal additionalPrice) {
        this.orderItem = orderItem;
        this.name = name;
        this.value = value;
        this.additionalPrice = additionalPrice;
    }
    
    // Getters and Setters
    public OrderItem getOrderItem() {
        return orderItem;
    }
    
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }
    
    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}
