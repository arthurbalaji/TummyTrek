package com.tummytrek.dto.response;

import java.math.BigDecimal;

public class VendorDashboardResponse {
    private Long restaurantId;
    private String restaurantName;
    private boolean isOpen;
    private boolean isAcceptingOrders;
    private Long totalOrders;
    private Long totalMenuItems;
    private Long todayOrders;
    private BigDecimal todayEarnings;
    private Long weekOrders;
    private BigDecimal weekEarnings;
    private Long monthOrders;
    private BigDecimal monthEarnings;
    private BigDecimal totalEarnings;
    private Long pendingOrders;
    private double averageRating;

    public VendorDashboardResponse() {}

    public VendorDashboardResponse(Long restaurantId, String restaurantName, boolean isOpen, 
                                 boolean isAcceptingOrders, Long totalOrders, Long totalMenuItems, 
                                 Long todayOrders, BigDecimal todayEarnings, Long weekOrders, 
                                 BigDecimal weekEarnings, Long monthOrders, BigDecimal monthEarnings, 
                                 BigDecimal totalEarnings, Long pendingOrders, double averageRating) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isOpen = isOpen;
        this.isAcceptingOrders = isAcceptingOrders;
        this.totalOrders = totalOrders;
        this.totalMenuItems = totalMenuItems;
        this.todayOrders = todayOrders;
        this.todayEarnings = todayEarnings;
        this.weekOrders = weekOrders;
        this.weekEarnings = weekEarnings;
        this.monthOrders = monthOrders;
        this.monthEarnings = monthEarnings;
        this.totalEarnings = totalEarnings;
        this.pendingOrders = pendingOrders;
        this.averageRating = averageRating;
    }

    public static VendorDashboardResponseBuilder builder() {
        return new VendorDashboardResponseBuilder();
    }

    // Getters and Setters
    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isAcceptingOrders() {
        return isAcceptingOrders;
    }

    public void setAcceptingOrders(boolean acceptingOrders) {
        isAcceptingOrders = acceptingOrders;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalMenuItems() {
        return totalMenuItems;
    }

    public void setTotalMenuItems(Long totalMenuItems) {
        this.totalMenuItems = totalMenuItems;
    }

    public Long getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public BigDecimal getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(BigDecimal todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public Long getWeekOrders() {
        return weekOrders;
    }

    public void setWeekOrders(Long weekOrders) {
        this.weekOrders = weekOrders;
    }

    public BigDecimal getWeekEarnings() {
        return weekEarnings;
    }

    public void setWeekEarnings(BigDecimal weekEarnings) {
        this.weekEarnings = weekEarnings;
    }

    public Long getMonthOrders() {
        return monthOrders;
    }

    public void setMonthOrders(Long monthOrders) {
        this.monthOrders = monthOrders;
    }

    public BigDecimal getMonthEarnings() {
        return monthEarnings;
    }

    public void setMonthEarnings(BigDecimal monthEarnings) {
        this.monthEarnings = monthEarnings;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(Long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // Builder class
    public static class VendorDashboardResponseBuilder {
        private Long restaurantId;
        private String restaurantName;
        private boolean isOpen;
        private boolean isAcceptingOrders;
        private Long totalOrders;
        private Long totalMenuItems;
        private Long todayOrders;
        private BigDecimal todayEarnings;
        private Long weekOrders;
        private BigDecimal weekEarnings;
        private Long monthOrders;
        private BigDecimal monthEarnings;
        private BigDecimal totalEarnings;
        private Long pendingOrders;
        private double averageRating;

        public VendorDashboardResponseBuilder restaurantId(Long restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public VendorDashboardResponseBuilder restaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
            return this;
        }

        public VendorDashboardResponseBuilder isOpen(boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        public VendorDashboardResponseBuilder isAcceptingOrders(boolean isAcceptingOrders) {
            this.isAcceptingOrders = isAcceptingOrders;
            return this;
        }

        public VendorDashboardResponseBuilder totalOrders(Long totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public VendorDashboardResponseBuilder totalMenuItems(Long totalMenuItems) {
            this.totalMenuItems = totalMenuItems;
            return this;
        }

        public VendorDashboardResponseBuilder todayOrders(Long todayOrders) {
            this.todayOrders = todayOrders;
            return this;
        }

        public VendorDashboardResponseBuilder todayEarnings(BigDecimal todayEarnings) {
            this.todayEarnings = todayEarnings;
            return this;
        }

        public VendorDashboardResponseBuilder weekOrders(Long weekOrders) {
            this.weekOrders = weekOrders;
            return this;
        }

        public VendorDashboardResponseBuilder weekEarnings(BigDecimal weekEarnings) {
            this.weekEarnings = weekEarnings;
            return this;
        }

        public VendorDashboardResponseBuilder monthOrders(Long monthOrders) {
            this.monthOrders = monthOrders;
            return this;
        }

        public VendorDashboardResponseBuilder monthEarnings(BigDecimal monthEarnings) {
            this.monthEarnings = monthEarnings;
            return this;
        }

        public VendorDashboardResponseBuilder totalEarnings(BigDecimal totalEarnings) {
            this.totalEarnings = totalEarnings;
            return this;
        }

        public VendorDashboardResponseBuilder pendingOrders(Long pendingOrders) {
            this.pendingOrders = pendingOrders;
            return this;
        }

        public VendorDashboardResponseBuilder averageRating(double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public VendorDashboardResponse build() {
            return new VendorDashboardResponse(restaurantId, restaurantName, isOpen, isAcceptingOrders,
                    totalOrders, totalMenuItems, todayOrders, todayEarnings, weekOrders, weekEarnings,
                    monthOrders, monthEarnings, totalEarnings, pendingOrders, averageRating);
        }
    }
}
