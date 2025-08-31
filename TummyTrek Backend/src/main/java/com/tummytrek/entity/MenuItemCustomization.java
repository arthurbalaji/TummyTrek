package com.tummytrek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_item_customizations")
public class MenuItemCustomization extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    
    @NotBlank(message = "Customization name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @NotNull(message = "Additional price is required")
    @DecimalMin(value = "0.0", message = "Additional price cannot be negative")
    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(name = "max_selections")
    private Integer maxSelections = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customization_type")
    private CustomizationType customizationType;
    
    @Column(name = "options", columnDefinition = "TEXT")
    private String options; // JSON string of available options
    
    // Default constructor
    public MenuItemCustomization() {}
    
    // Constructor
    public MenuItemCustomization(MenuItem menuItem, String name, BigDecimal additionalPrice, 
                               CustomizationType customizationType) {
        this.menuItem = menuItem;
        this.name = name;
        this.additionalPrice = additionalPrice;
        this.customizationType = customizationType;
    }
    
    // Getters and Setters
    public MenuItem getMenuItem() {
        return menuItem;
    }
    
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }
    
    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public Integer getMaxSelections() {
        return maxSelections;
    }
    
    public void setMaxSelections(Integer maxSelections) {
        this.maxSelections = maxSelections;
    }
    
    public CustomizationType getCustomizationType() {
        return customizationType;
    }
    
    public void setCustomizationType(CustomizationType customizationType) {
        this.customizationType = customizationType;
    }
    
    public String getOptions() {
        return options;
    }
    
    public void setOptions(String options) {
        this.options = options;
    }
}
