package com.tummytrek.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner extends BaseEntity {
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;
    
    @Column(name = "license_number")
    private String licenseNumber;
    
    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Column(name = "aadhar_number")
    private String aadharNumber;
    
    @Column(name = "pan_number")
    private String panNumber;
    
    @Column(name = "bank_account_number")
    private String bankAccountNumber;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "ifsc_code")
    private String ifscCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryPartnerStatus status = DeliveryPartnerStatus.PENDING_APPROVAL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;
    
    @Column(name = "is_on_duty")
    private Boolean isOnDuty = false;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;
    
    @Column(name = "shift_start_time")
    private LocalDateTime shiftStartTime;
    
    @Column(name = "shift_end_time")
    private LocalDateTime shiftEndTime;
    
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;
    
    @Column(name = "total_earnings", precision = 12, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    
    @Column(name = "monthly_salary", precision = 10, scale = 2)
    private BigDecimal monthlySalary = BigDecimal.ZERO;
    
    @Column(name = "incentive_rate", precision = 5, scale = 2)
    private BigDecimal incentiveRate = BigDecimal.ZERO; // Per delivery incentive
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "is_available_for_orders")
    private Boolean isAvailableForOrders = false;
    
    @Column(name = "max_concurrent_orders")
    private Integer maxConcurrentOrders = 1;
    
    @Column(name = "current_order_count")
    private Integer currentOrderCount = 0;
    
    @OneToMany(mappedBy = "deliveryPartner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "deliveryPartner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryPartnerReview> reviews = new ArrayList<>();
    
    // Default constructor
    public DeliveryPartner() {}
    
    // Constructor
    public DeliveryPartner(User user, String employeeId) {
        this.user = user;
        this.employeeId = employeeId;
    }
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }
    
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public String getAadharNumber() {
        return aadharNumber;
    }
    
    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }
    
    public String getPanNumber() {
        return panNumber;
    }
    
    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
    
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
    
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getIfscCode() {
        return ifscCode;
    }
    
    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
    
    public DeliveryPartnerStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeliveryPartnerStatus status) {
        this.status = status;
    }
    
    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }
    
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
    
    public Boolean getIsOnDuty() {
        return isOnDuty;
    }
    
    public void setIsOnDuty(Boolean isOnDuty) {
        this.isOnDuty = isOnDuty;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }
    
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }
    
    public LocalDateTime getShiftStartTime() {
        return shiftStartTime;
    }
    
    public void setShiftStartTime(LocalDateTime shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }
    
    public LocalDateTime getShiftEndTime() {
        return shiftEndTime;
    }
    
    public void setShiftEndTime(LocalDateTime shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public Integer getTotalDeliveries() {
        return totalDeliveries;
    }
    
    public void setTotalDeliveries(Integer totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }
    
    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }
    
    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
    
    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }
    
    public void setMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary;
    }
    
    public BigDecimal getIncentiveRate() {
        return incentiveRate;
    }
    
    public void setIncentiveRate(BigDecimal incentiveRate) {
        this.incentiveRate = incentiveRate;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public Boolean getIsAvailableForOrders() {
        return isAvailableForOrders;
    }
    
    public void setIsAvailableForOrders(Boolean isAvailableForOrders) {
        this.isAvailableForOrders = isAvailableForOrders;
    }
    
    public Integer getMaxConcurrentOrders() {
        return maxConcurrentOrders;
    }
    
    public void setMaxConcurrentOrders(Integer maxConcurrentOrders) {
        this.maxConcurrentOrders = maxConcurrentOrders;
    }
    
    public Integer getCurrentOrderCount() {
        return currentOrderCount;
    }
    
    public void setCurrentOrderCount(Integer currentOrderCount) {
        this.currentOrderCount = currentOrderCount;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    public List<DeliveryPartnerReview> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<DeliveryPartnerReview> reviews) {
        this.reviews = reviews;
    }
}
