package com.tummytrek.dto.response;

public class VendorOrderStatsResponse {
    private Long placed;
    private Long confirmed;
    private Long preparing;
    private Long ready;
    private Long pickedUp;
    private Long delivered;
    private Long cancelled;

    public VendorOrderStatsResponse() {}

    public VendorOrderStatsResponse(Long placed, Long confirmed, Long preparing, Long ready, 
                                  Long pickedUp, Long delivered, Long cancelled) {
        this.placed = placed;
        this.confirmed = confirmed;
        this.preparing = preparing;
        this.ready = ready;
        this.pickedUp = pickedUp;
        this.delivered = delivered;
        this.cancelled = cancelled;
    }

    public static VendorOrderStatsResponseBuilder builder() {
        return new VendorOrderStatsResponseBuilder();
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

    public Long getReady() {
        return ready;
    }

    public void setReady(Long ready) {
        this.ready = ready;
    }

    public Long getPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(Long pickedUp) {
        this.pickedUp = pickedUp;
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
    public static class VendorOrderStatsResponseBuilder {
        private Long placed;
        private Long confirmed;
        private Long preparing;
        private Long ready;
        private Long pickedUp;
        private Long delivered;
        private Long cancelled;

        public VendorOrderStatsResponseBuilder placed(Long placed) {
            this.placed = placed;
            return this;
        }

        public VendorOrderStatsResponseBuilder confirmed(Long confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public VendorOrderStatsResponseBuilder preparing(Long preparing) {
            this.preparing = preparing;
            return this;
        }

        public VendorOrderStatsResponseBuilder ready(Long ready) {
            this.ready = ready;
            return this;
        }

        public VendorOrderStatsResponseBuilder pickedUp(Long pickedUp) {
            this.pickedUp = pickedUp;
            return this;
        }

        public VendorOrderStatsResponseBuilder delivered(Long delivered) {
            this.delivered = delivered;
            return this;
        }

        public VendorOrderStatsResponseBuilder cancelled(Long cancelled) {
            this.cancelled = cancelled;
            return this;
        }

        public VendorOrderStatsResponse build() {
            return new VendorOrderStatsResponse(placed, confirmed, preparing, ready, 
                    pickedUp, delivered, cancelled);
        }
    }
}
