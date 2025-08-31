package com.tummytrek.dto.response;

import java.math.BigDecimal;

public class RevenueDataResponse {
    private String date;
    private BigDecimal revenue;
    private Long orders;
    
    public RevenueDataResponse() {}
    
    public RevenueDataResponse(String date, BigDecimal revenue, Long orders) {
        this.date = date;
        this.revenue = revenue;
        this.orders = orders;
    }
    
    public static RevenueDataResponseBuilder builder() {
        return new RevenueDataResponseBuilder();
    }
    
    // Getters and Setters
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public BigDecimal getRevenue() {
        return revenue;
    }
    
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
    
    public Long getOrders() {
        return orders;
    }
    
    public void setOrders(Long orders) {
        this.orders = orders;
    }
    
    // Builder class
    public static class RevenueDataResponseBuilder {
        private String date;
        private BigDecimal revenue;
        private Long orders;
        
        public RevenueDataResponseBuilder date(String date) {
            this.date = date;
            return this;
        }
        
        public RevenueDataResponseBuilder revenue(BigDecimal revenue) {
            this.revenue = revenue;
            return this;
        }
        
        public RevenueDataResponseBuilder orders(Long orders) {
            this.orders = orders;
            return this;
        }
        
        public RevenueDataResponse build() {
            return new RevenueDataResponse(date, revenue, orders);
        }
    }
}
