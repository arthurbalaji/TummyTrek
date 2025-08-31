package com.tummytrek.dto.response;

import java.math.BigDecimal;

public class DashboardStatsResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long totalCustomers;
    private Long totalRestaurants;
    private Long totalDeliveryPartners;
    private Long todayOrders;
    private BigDecimal todayRevenue;
    private Long pendingApprovals;
    
    public DashboardStatsResponse() {}
    
    public DashboardStatsResponse(Long totalOrders, BigDecimal totalRevenue, Long totalCustomers,
                                 Long totalRestaurants, Long totalDeliveryPartners, Long todayOrders,
                                 BigDecimal todayRevenue, Long pendingApprovals) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalCustomers = totalCustomers;
        this.totalRestaurants = totalRestaurants;
        this.totalDeliveryPartners = totalDeliveryPartners;
        this.todayOrders = todayOrders;
        this.todayRevenue = todayRevenue;
        this.pendingApprovals = pendingApprovals;
    }
    
    public static DashboardStatsResponseBuilder builder() {
        return new DashboardStatsResponseBuilder();
    }
    
    // Getters and Setters
    public Long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Long getTotalCustomers() {
        return totalCustomers;
    }
    
    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    
    public Long getTotalRestaurants() {
        return totalRestaurants;
    }
    
    public void setTotalRestaurants(Long totalRestaurants) {
        this.totalRestaurants = totalRestaurants;
    }
    
    public Long getTotalDeliveryPartners() {
        return totalDeliveryPartners;
    }
    
    public void setTotalDeliveryPartners(Long totalDeliveryPartners) {
        this.totalDeliveryPartners = totalDeliveryPartners;
    }
    
    public Long getTodayOrders() {
        return todayOrders;
    }
    
    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }
    
    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }
    
    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }
    
    public Long getPendingApprovals() {
        return pendingApprovals;
    }
    
    public void setPendingApprovals(Long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }
    
    // Builder class
    public static class DashboardStatsResponseBuilder {
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private Long totalCustomers;
        private Long totalRestaurants;
        private Long totalDeliveryPartners;
        private Long todayOrders;
        private BigDecimal todayRevenue;
        private Long pendingApprovals;
        
        public DashboardStatsResponseBuilder totalOrders(Long totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }
        
        public DashboardStatsResponseBuilder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }
        
        public DashboardStatsResponseBuilder totalCustomers(Long totalCustomers) {
            this.totalCustomers = totalCustomers;
            return this;
        }
        
        public DashboardStatsResponseBuilder totalRestaurants(Long totalRestaurants) {
            this.totalRestaurants = totalRestaurants;
            return this;
        }
        
        public DashboardStatsResponseBuilder totalDeliveryPartners(Long totalDeliveryPartners) {
            this.totalDeliveryPartners = totalDeliveryPartners;
            return this;
        }
        
        public DashboardStatsResponseBuilder todayOrders(Long todayOrders) {
            this.todayOrders = todayOrders;
            return this;
        }
        
        public DashboardStatsResponseBuilder todayRevenue(BigDecimal todayRevenue) {
            this.todayRevenue = todayRevenue;
            return this;
        }
        
        public DashboardStatsResponseBuilder pendingApprovals(Long pendingApprovals) {
            this.pendingApprovals = pendingApprovals;
            return this;
        }
        
        public DashboardStatsResponse build() {
            return new DashboardStatsResponse(totalOrders, totalRevenue, totalCustomers,
                    totalRestaurants, totalDeliveryPartners, todayOrders,
                    todayRevenue, pendingApprovals);
        }
    }
}
