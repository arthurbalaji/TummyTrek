package com.tummytrek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
public class OrderStatusHistory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    
    @Column(name = "status_changed_at", nullable = false)
    private LocalDateTime statusChangedAt;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "changed_by")
    private String changedBy; // SYSTEM, CUSTOMER, RESTAURANT, DELIVERY_PARTNER, ADMIN
    
    // Default constructor
    public OrderStatusHistory() {}
    
    // Constructor
    public OrderStatusHistory(Order order, OrderStatus status, LocalDateTime statusChangedAt, String changedBy) {
        this.order = order;
        this.status = status;
        this.statusChangedAt = statusChangedAt;
        this.changedBy = changedBy;
    }
    
    // Getters and Setters
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStatusChangedAt() {
        return statusChangedAt;
    }
    
    public void setStatusChangedAt(LocalDateTime statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public String getChangedBy() {
        return changedBy;
    }
    
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}
