package com.tummytrek.service;

import com.tummytrek.dto.request.OrderRequest;
import com.tummytrek.entity.*;
import com.tummytrek.exception.ResourceNotFoundException;
import com.tummytrek.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    
    public Order createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        // Generate unique order number
        String orderNumber = "TT" + System.currentTimeMillis();
        
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PLACED);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setDeliveryInstructions(request.getDeliveryInstructions());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPromoCode(request.getPromoCode());
        
        // Calculate amounts and create order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
            
            // Check if menu item is available
            if (!menuItem.getIsAvailable() || !menuItem.getIsActive()) {
                throw new IllegalStateException("Menu item " + menuItem.getName() + " is not available");
            }
            
            // Validate quantity
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            BigDecimal itemPrice = menuItem.getDiscountedPrice() != null ? 
                menuItem.getDiscountedPrice() : menuItem.getPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        
        order.setSubtotal(subtotal);
        order.setDeliveryFee(restaurant.getDeliveryFee() != null ? restaurant.getDeliveryFee() : BigDecimal.ZERO);
        order.setPlatformFee(subtotal.multiply(BigDecimal.valueOf(0.02))); // 2% platform fee
        order.setTaxAmount(subtotal.multiply(BigDecimal.valueOf(0.05))); // 5% tax
        order.setDiscountAmount(BigDecimal.ZERO); // Initialize discount amount
        
        BigDecimal totalAmount = subtotal
            .add(order.getDeliveryFee())
            .add(order.getPlatformFee())
            .add(order.getTaxAmount())
            .subtract(order.getDiscountAmount());
        
        order.setTotalAmount(totalAmount);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(restaurant.getDeliveryTimeMax()));
        order.setDeliveryOtp(generateOTP());
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        for (var itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            
            // Use discounted price if available
            BigDecimal unitPrice = menuItem.getDiscountedPrice() != null ? 
                menuItem.getDiscountedPrice() : menuItem.getPrice();
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem.setSpecialInstructions(itemRequest.getSpecialInstructions());
            
            savedOrder.getOrderItems().add(orderItem);
            
            // Increment menu item order count
            menuItem.setTotalOrders((menuItem.getTotalOrders() != null ? menuItem.getTotalOrders() : 0) + itemRequest.getQuantity());
            menuItemRepository.save(menuItem);
        }
        
        // Create status history
        createOrderStatusHistory(savedOrder, OrderStatus.PLACED, "Order placed by customer");
        
        return orderRepository.save(savedOrder);
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }
    
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
    }
    
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
    
    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }
    
    public List<Order> getOrdersByDeliveryPartner(Long deliveryPartnerId) {
        return orderRepository.findByDeliveryPartnerIdOrderByCreatedAtDesc(deliveryPartnerId);
    }
    
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtAsc(
            List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PREPARING));
    }
    
    public Order updateOrderStatus(Long orderId, OrderStatus status, String remarks) {
        Order order = getOrderById(orderId);
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);
        
        // Update timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case CONFIRMED:
                break;
            case PREPARING:
                order.setCookingStartedAt(now);
                break;
            case READY_FOR_PICKUP:
                order.setReadyForPickupAt(now);
                break;
            case PICKED_UP:
                order.setPickedUpAt(now);
                break;
            case OUT_FOR_DELIVERY:
                order.setOutForDeliveryAt(now);
                break;
            case DELIVERED:
                order.setDeliveredAt(now);
                order.setActualDeliveryTime(now);
                break;
            case CANCELLED:
                order.setCancelledAt(now);
                break;
        }
        
        Order savedOrder = orderRepository.save(order);
        createOrderStatusHistory(savedOrder, status, remarks);
        
        return savedOrder;
    }
    
    public Order assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        Order order = getOrderById(orderId);
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(deliveryPartnerId)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        
        order.setDeliveryPartner(deliveryPartner);
        return orderRepository.save(order);
    }
    
    public Order cancelOrder(Long orderId, String reason, String cancelledBy) {
        Order order = getOrderById(orderId);
        
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel delivered order");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);
        order.setCancelledBy(cancelledBy);
        
        Order savedOrder = orderRepository.save(order);
        createOrderStatusHistory(savedOrder, OrderStatus.CANCELLED, reason);
        
        return savedOrder;
    }
    
    private void createOrderStatusHistory(Order order, OrderStatus status, String remarks) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setStatusChangedAt(LocalDateTime.now());
        history.setRemarks(remarks);
        history.setChangedBy("SYSTEM");
        
        order.getStatusHistory().add(history);
    }
    
    private String generateOTP() {
        return String.format("%04d", (int) (Math.random() * 10000));
    }
    
    public List<Order> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);
    }
    
    public BigDecimal getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.sumTotalAmountByCreatedAtBetween(startOfDay, endOfDay);
    }
    
    // Missing methods for controllers
    public long countByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> findAllOrders(int page, int size) {
        return orderRepository.findAll();
    }
    
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = orderRepository.sumTotalAmountByCreatedAtBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }
}
