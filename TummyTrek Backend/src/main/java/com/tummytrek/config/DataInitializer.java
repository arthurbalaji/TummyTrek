package com.tummytrek.config;

import com.tummytrek.entity.*;
import com.tummytrek.repository.MenuItemRepository;
import com.tummytrek.repository.NotificationRepository;
import com.tummytrek.repository.RestaurantRepository;
import com.tummytrek.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        createAdminUser();
        createSampleNotifications();
    }
    
    private void createAdminUser() {
        try {
            // Check if admin user already exists
            if (!userRepository.existsByEmail("admin@tummytrek.com")) {
                User adminUser = new User();
                adminUser.setName("Admin User");
                adminUser.setEmail("admin@tummytrek.com");
                adminUser.setPhone("+1234567890");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRole(UserRole.ADMIN);
                adminUser.setStatus(UserStatus.ACTIVE);
                adminUser.setEmailVerified(true);
                adminUser.setPhoneVerified(true);
                
                userRepository.save(adminUser);
                
                logger.info("✅ Admin user created successfully!");
                logger.info("📧 Email: admin@tummytrek.com");
                logger.info("🔑 Password: admin123");
                logger.info("⚠️  Please change the password after first login!");
            } else {
                logger.info("ℹ️  Admin user already exists, skipping creation.");
            }
        } catch (Exception e) {
            logger.error("❌ Error creating admin user: {}", e.getMessage());
        }
    }
    
    private void createSampleNotifications() {
        try {
            if (notificationRepository.count() == 0) {
                // Get the admin user to associate notifications with
                User adminUser = userRepository.findByEmail("admin@tummytrek.com")
                    .orElse(null);
                
                if (adminUser == null) {
                    logger.error("❌ Admin user not found. Cannot create notifications.");
                    return;
                }
                
                // Create sample notifications and associate them with admin user
                Notification notification1 = new Notification(
                    Notification.NotificationType.ORDER,
                    "New Order Received",
                    "Order #TT-2024-1234 from John Doe at Spice Garden",
                    Notification.NotificationPriority.HIGH
                );
                notification1.setUser(adminUser);
                
                Notification notification2 = new Notification(
                    Notification.NotificationType.RESTAURANT,
                    "Restaurant Application",
                    "New restaurant 'Italian Corner' applied for approval",
                    Notification.NotificationPriority.MEDIUM
                );
                notification2.setUser(adminUser);
                
                Notification notification3 = new Notification(
                    Notification.NotificationType.COMPLAINT,
                    "Customer Complaint",
                    "Customer reported delivery delay for order #TT-2024-1230",
                    Notification.NotificationPriority.HIGH
                );
                notification3.setUser(adminUser);
                
                Notification notification4 = new Notification(
                    Notification.NotificationType.DELIVERY_PARTNER,
                    "Delivery Partner Joined",
                    "Sarah Smith has successfully completed onboarding",
                    Notification.NotificationPriority.LOW
                );
                notification4.setUser(adminUser);
                
                notificationRepository.save(notification1);
                notificationRepository.save(notification2);
                notificationRepository.save(notification3);
                notificationRepository.save(notification4);
                
                logger.info("✅ Sample notifications created successfully for admin user!");
            } else {
                logger.info("ℹ️  Notifications already exist, skipping creation.");
            }
        } catch (Exception e) {
            logger.error("❌ Error creating sample notifications: {}", e.getMessage());
        }
    }
}
