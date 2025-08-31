package com.tummytrek.dto.response;

public class OrderStatsResponse {
    private Long placed;
    private Long confirmed;
    private Long preparing;
    private Long readyForPickup;
    private Long pickedUp;
    private Long outForDelivery;
    private Long delivered;
    private Long cancelled;
    
    public OrderStatsResponse() {}
    
    public OrderStatsResponse(Long placed, Long confirmed, Long preparing, Long readyForPickup,
                             Long pickedUp, Long outForDelivery, Long delivered, Long cancelled) {
        this.placed = placed;
        this.confirmed = confirmed;
        this.preparing = preparing;
        this.readyForPickup = readyForPickup;
        this.pickedUp = pickedUp;
        this.outForDelivery = outForDelivery;
        this.delivered = delivered;
        this.cancelled = cancelled;
    }
    
    public static OrderStatsResponseBuilder builder() {
        return new OrderStatsResponseBuilder();
    }
    
    // Getters and Setters
    public Long getPlaced() {
        return placed;
    }
    
    public void setPlaced(Long placed) {
        this.placed = placed;
    }
    
    public Long getConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(Long confirmed) {
        this.confirmed = confirmed;
    }
    
    public Long getPreparing() {
        return preparing;
    }
    
    public void setPreparing(Long preparing) {
        this.preparing = preparing;
    }
    
    public Long getReadyForPickup() {
        return readyForPickup;
    }
    
    public void setReadyForPickup(Long readyForPickup) {
        this.readyForPickup = readyForPickup;
    }
    
    public Long getPickedUp() {
        return pickedUp;
    }
    
    public void setPickedUp(Long pickedUp) {
        this.pickedUp = pickedUp;
    }
    
    public Long getOutForDelivery() {
        return outForDelivery;
    }
    
    public void setOutForDelivery(Long outForDelivery) {
        this.outForDelivery = outForDelivery;
    }
    
    public Long getDelivered() {
        return delivered;
    }
    
    public void setDelivered(Long delivered) {
        this.delivered = delivered;
    }
    
    public Long getCancelled() {
        return cancelled;
    }
    
    public void setCancelled(Long cancelled) {
        this.cancelled = cancelled;
    }
    
    // Builder class
    public static class OrderStatsResponseBuilder {
        private Long placed;
        private Long confirmed;
        private Long preparing;
        private Long readyForPickup;
        private Long pickedUp;
        private Long outForDelivery;
        private Long delivered;
        private Long cancelled;
        
        public OrderStatsResponseBuilder placed(Long placed) {
            this.placed = placed;
            return this;
        }
        
        public OrderStatsResponseBuilder confirmed(Long confirmed) {
            this.confirmed = confirmed;
            return this;
        }
        
        public OrderStatsResponseBuilder preparing(Long preparing) {
            this.preparing = preparing;
            return this;
        }
        
        public OrderStatsResponseBuilder readyForPickup(Long readyForPickup) {
            this.readyForPickup = readyForPickup;
            return this;
        }
        
        public OrderStatsResponseBuilder pickedUp(Long pickedUp) {
            this.pickedUp = pickedUp;
            return this;
        }
        
        public OrderStatsResponseBuilder outForDelivery(Long outForDelivery) {
            this.outForDelivery = outForDelivery;
            return this;
        }
        
        public OrderStatsResponseBuilder delivered(Long delivered) {
            this.delivered = delivered;
            return this;
        }
        
        public OrderStatsResponseBuilder cancelled(Long cancelled) {
            this.cancelled = cancelled;
            return this;
        }
        
        public OrderStatsResponse build() {
            return new OrderStatsResponse(placed, confirmed, preparing, readyForPickup,
                    pickedUp, outForDelivery, delivered, cancelled);
        }
    }
}
