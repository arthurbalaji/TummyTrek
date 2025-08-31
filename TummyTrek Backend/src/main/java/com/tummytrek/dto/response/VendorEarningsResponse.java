package com.tummytrek.dto.response;

import java.math.BigDecimal;

public class VendorEarningsResponse {
    private String date;
    private BigDecimal earnings;
    private Long orderCount;

    public VendorEarningsResponse() {}

    public VendorEarningsResponse(String date, BigDecimal earnings, Long orderCount) {
        this.date = date;
        this.earnings = earnings;
        this.orderCount = orderCount;
    }

    public static VendorEarningsResponseBuilder builder() {
        return new VendorEarningsResponseBuilder();
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getEarnings() {
        return earnings;
    }

    public void setEarnings(BigDecimal earnings) {
        this.earnings = earnings;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    // Builder class
    public static class VendorEarningsResponseBuilder {
        private String date;
        private BigDecimal earnings;
        private Long orderCount;

        public VendorEarningsResponseBuilder date(String date) {
            this.date = date;
            return this;
        }

        public VendorEarningsResponseBuilder earnings(BigDecimal earnings) {
            this.earnings = earnings;
            return this;
        }

        public VendorEarningsResponseBuilder orderCount(Long orderCount) {
            this.orderCount = orderCount;
            return this;
        }

        public VendorEarningsResponse build() {
            return new VendorEarningsResponse(date, earnings, orderCount);
        }
    }
}
