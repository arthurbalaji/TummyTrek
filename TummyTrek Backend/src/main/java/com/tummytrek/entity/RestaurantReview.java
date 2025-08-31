package com.tummytrek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "restaurant_reviews")
public class RestaurantReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
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
    
    @Column(name = "food_quality_rating")
    @Min(value = 1, message = "Food quality rating must be at least 1")
    @Max(value = 5, message = "Food quality rating cannot exceed 5")
    private Integer foodQualityRating;
    
    @Column(name = "delivery_time_rating")
    @Min(value = 1, message = "Delivery time rating must be at least 1")
    @Max(value = 5, message = "Delivery time rating cannot exceed 5")
    private Integer deliveryTimeRating;
    
    @Column(name = "packaging_rating")
    @Min(value = 1, message = "Packaging rating must be at least 1")
    @Max(value = 5, message = "Packaging rating cannot exceed 5")
    private Integer packagingRating;
    
    @Column(name = "would_recommend")
    private Boolean wouldRecommend = true;
    
    // Default constructor
    public RestaurantReview() {}
    
    // Constructor
    public RestaurantReview(Customer customer, Restaurant restaurant, Order order, Integer rating) {
        this.customer = customer;
        this.restaurant = restaurant;
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
    
    public Restaurant getRestaurant() {
        return restaurant;
    }
    
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
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
    
    public Integer getFoodQualityRating() {
        return foodQualityRating;
    }
    
    public void setFoodQualityRating(Integer foodQualityRating) {
        this.foodQualityRating = foodQualityRating;
    }
    
    public Integer getDeliveryTimeRating() {
        return deliveryTimeRating;
    }
    
    public void setDeliveryTimeRating(Integer deliveryTimeRating) {
        this.deliveryTimeRating = deliveryTimeRating;
    }
    
    public Integer getPackagingRating() {
        return packagingRating;
    }
    
    public void setPackagingRating(Integer packagingRating) {
        this.packagingRating = packagingRating;
    }
    
    public Boolean getWouldRecommend() {
        return wouldRecommend;
    }
    
    public void setWouldRecommend(Boolean wouldRecommend) {
        this.wouldRecommend = wouldRecommend;
    }
}
