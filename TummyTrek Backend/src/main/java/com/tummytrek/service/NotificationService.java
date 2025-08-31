package com.tummytrek.service;

import com.tummytrek.entity.Notification;
import com.tummytrek.entity.Order;
import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import com.tummytrek.dto.NotificationResponse;
import com.tummytrek.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public void sendOrderUpdate(Order order) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "ORDER_UPDATE");
        notification.put("orderId", order.getId());
        notification.put("orderNumber", order.getOrderNumber());
        notification.put("status", order.getStatus());
        notification.put("timestamp", LocalDateTime.now());
        
        // Send to customer
        messagingTemplate.convertAndSendToUser(
            order.getCustomer().getUser().getId().toString(),
            "/queue/notifications",
            notification
        );
        
        // Send to restaurant
        messagingTemplate.convertAndSendToUser(
            order.getRestaurant().getVendor().getId().toString(),
            "/queue/notifications",
            notification
        );
        
        // Send to delivery partner if assigned
        if (order.getDeliveryPartner() != null) {
            messagingTemplate.convertAndSendToUser(
                order.getDeliveryPartner().getUser().getId().toString(),
                "/queue/notifications",
                notification
            );
        }
        
        // Send to all admins
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
    }
    
    public void sendNewOrderNotification(Order order) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_ORDER");
        notification.put("orderId", order.getId());
        notification.put("orderNumber", order.getOrderNumber());
        notification.put("customerName", order.getCustomer().getUser().getName());
        notification.put("restaurantName", order.getRestaurant().getName());
        notification.put("totalAmount", order.getTotalAmount());
        notification.put("timestamp", LocalDateTime.now());
        
        // Send to restaurant
        messagingTemplate.convertAndSendToUser(
            order.getRestaurant().getVendor().getId().toString(),
            "/queue/notifications",
            notification
        );
        
        // Send to all admins
        messagingTemplate.convertAndSend("/topic/admin/orders", notification);
    }
    
    public void sendDeliveryAssignmentNotification(Order order) {
        if (order.getDeliveryPartner() == null) return;
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "DELIVERY_ASSIGNMENT");
        notification.put("orderId", order.getId());
        notification.put("orderNumber", order.getOrderNumber());
        notification.put("restaurantName", order.getRestaurant().getName());
        notification.put("deliveryAddress", order.getDeliveryAddress());
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSendToUser(
            order.getDeliveryPartner().getUser().getId().toString(),
            "/queue/notifications",
            notification
        );
    }
    
    public void sendSystemNotification(String message, UserRole targetRole) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SYSTEM_NOTIFICATION");
        notification.put("message", message);
        notification.put("timestamp", LocalDateTime.now());
        
        String topic = switch (targetRole) {
            case ADMIN -> "/topic/admin/system";
            case VENDOR -> "/topic/vendor/system";
            case DELIVERY_PARTNER -> "/topic/delivery/system";
            case CUSTOMER -> "/topic/customer/system";
        };
        
        messagingTemplate.convertAndSend(topic, notification);
    }
    
    public void sendDashboardUpdate() {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "DASHBOARD_UPDATE");
        notification.put("timestamp", LocalDateTime.now());
        
        messagingTemplate.convertAndSend("/topic/admin/dashboard", notification);
    }
    
    // Database notification methods
    public List<NotificationResponse> getAllNotifications() {
        // For now, return all notifications. In a real app, you'd filter by current user
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponse> getAllNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponse> getUnreadNotifications() {
        List<Notification> notifications = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    public List<NotificationResponse> getUnreadNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markNotificationAsRead(Long id) {
        notificationRepository.markAsRead(id);
    }
    
    @Transactional
    public void markAllNotificationsAsRead() {
        // For now, mark all notifications as read. In a real app, you'd use user ID
        notificationRepository.markAllAsReadByUserId(1L); // Assuming admin user ID is 1
    }
    
    @Transactional
    public void markAllNotificationsAsReadForUser(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
    
    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    
    @Transactional
    public void createNotification(Notification.NotificationType type, String title, String message, Notification.NotificationPriority priority) {
        Notification notification = new Notification(type, title, message, priority);
        notificationRepository.save(notification);
    }
    
    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }
}
