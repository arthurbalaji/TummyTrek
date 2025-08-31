package com.tummytrek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "delivery_partner_reviews")
public class DeliveryPartnerReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id", nullable = false)
    private DeliveryPartner deliveryPartner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "punctuality_rating")
    @Min(value = 1, message = "Punctuality rating must be at least 1")
    @Max(value = 5, message = "Punctuality rating cannot exceed 5")
    private Integer punctualityRating;
    
    @Column(name = "behavior_rating")
    @Min(value = 1, message = "Behavior rating must be at least 1")
    @Max(value = 5, message = "Behavior rating cannot exceed 5")
    private Integer behaviorRating;
    
    // Default constructor
    public DeliveryPartnerReview() {}
    
    // Constructor
    public DeliveryPartnerReview(Customer customer, DeliveryPartner deliveryPartner, Order order, Integer rating) {
        this.customer = customer;
        this.deliveryPartner = deliveryPartner;
        this.order = order;
        this.rating = rating;
    }
    
    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public DeliveryPartner getDeliveryPartner() {
        return deliveryPartner;
    }
    
    public void setDeliveryPartner(DeliveryPartner deliveryPartner) {
        this.deliveryPartner = deliveryPartner;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Integer getPunctualityRating() {
        return punctualityRating;
    }
    
    public void setPunctualityRating(Integer punctualityRating) {
        this.punctualityRating = punctualityRating;
    }
    
    public Integer getBehaviorRating() {
        return behaviorRating;
    }
    
    public void setBehaviorRating(Integer behaviorRating) {
        this.behaviorRating = behaviorRating;
    }
}
