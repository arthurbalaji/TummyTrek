package com.tummytrek.service;

import com.tummytrek.dto.request.UserRegistrationRequest;
import com.tummytrek.dto.request.LoginRequest;
import com.tummytrek.dto.response.LoginResponse;
import com.tummytrek.dto.response.UserResponse;
import com.tummytrek.entity.User;
import com.tummytrek.entity.UserRole;
import com.tummytrek.entity.UserStatus;
import com.tummytrek.entity.Customer;
import com.tummytrek.entity.DeliveryPartner;
import com.tummytrek.repository.UserRepository;
import com.tummytrek.repository.CustomerRepository;
import com.tummytrek.repository.DeliveryPartnerRepository;
import com.tummytrek.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Validate password strength
        validatePasswordStrength(request.getPassword());
        
        // Check if user already exists
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        // Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus(UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // Create role-specific entities
        if (savedUser.getRole() == UserRole.CUSTOMER) {
            Customer customer = new Customer(savedUser);
            customerRepository.save(customer);
        } else if (savedUser.getRole() == UserRole.DELIVERY_PARTNER) {
            DeliveryPartner deliveryPartner = new DeliveryPartner(savedUser, generateEmployeeId());
            deliveryPartnerRepository.save(deliveryPartner);
        }
        
        return convertToUserResponse(savedUser);
    }
    
    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByEmailOrPhone(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        String token = jwtUtil.generateToken(userDetails, extraClaims);
        
        return new LoginResponse(token, 86400000L, convertToUserResponse(user));
    }
    
    public UserResponse getUserProfile(String username) {
        User user = userRepository.findByEmailOrPhone(username, username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return convertToUserResponse(user);
    }
    
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus().name());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setEmailVerified(user.getEmailVerified());
        response.setPhoneVerified(user.getPhoneVerified());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    private String generateEmployeeId() {
        return "EMP" + System.currentTimeMillis();
    }
    
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(ch) >= 0);
        
        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
            throw new IllegalArgumentException(
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
    }
}
